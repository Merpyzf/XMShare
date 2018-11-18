package com.merpyzf.xmshare.ui.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.transfermanager.PeerManager;
import com.merpyzf.transfermanager.entity.Peer;
import com.merpyzf.transfermanager.entity.SignMessage;
import com.merpyzf.transfermanager.interfaces.OnPeerActionListener;
import com.merpyzf.transfermanager.util.ApManager;
import com.merpyzf.transfermanager.util.NetworkUtil;
import com.merpyzf.transfermanager.util.WifiMgr;
import com.merpyzf.transfermanager.util.timer.OSTimer;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.ui.adapter.PeerAdapter;
import com.merpyzf.xmshare.ui.activity.InputHotspotPwdActivity;
import com.merpyzf.xmshare.ui.interfaces.OnPairActionListener;
import com.merpyzf.xmshare.util.SharedPreUtils;
import com.merpyzf.xmshare.util.SingleThreadPool;
import com.merpyzf.xmshare.util.ToastUtils;
import com.merpyzf.xmshare.util.UiUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 扫描附近的设备
 *
 * @author wangke
 */
public class ScanPeerFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.rv_peers)
    RecyclerView mRvPeerList;
    @BindView(R.id.tv_tip)
    TextView mTvTip;

    private PeerManager mPeerManager;
    private List<Peer> mPeerList = new ArrayList<>();
    private PeerAdapter mPeerAdapter;
    private OnPairActionListener mOnPairActionListener;
    private Peer mPeerRequestConn;
    private WifiMgr mWifiMgr;
    private String mLocalAddress;
    private ScanPeerHandler mHandler;
    private OSTimer mScanWifiTimer;
    private boolean isStopScan;
    private static final int TYPE_SCAN_WIFI = 1;
    private static final int TYPE_SEND_FILE = 2;
    private static final int TYPE_GET_IP = 3;
    private static final int TYPE_GET_IP_FAILED = 4;
    private static final String TAG = ScanPeerFragment.class.getSimpleName();

    public ScanPeerFragment() {
        isStopScan = false;
    }


    @Override
    protected void initWidget(View rootView) {
        mTvTip.setTextColor(Color.WHITE);
        mTvTip.setText("正在扫描周围的接收者...");
        mRvPeerList.setLayoutManager(new LinearLayoutManager(mContext));
        mPeerAdapter = new PeerAdapter(R.layout.item_rv_send_peer, mPeerList);
        mRvPeerList.setAdapter(mPeerAdapter);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_scan_peer;
    }

    @Override
    protected void initEvent() {
        initWifiState();
        // 设置RecyclerView的点击事件
        mPeerAdapter.setOnItemClickListener(this);
        mHandler = new ScanPeerHandler(this);
        // 开启一个udp server 用于和局域网内的设备进行交互
        //todo 这个接口需要重新设计
        mPeerManager = new PeerManager(mContext, App.getNickname());
        mPeerManager.setOnPeerActionListener(new OnPeerActionListener() {
            @Override
            public void onDeviceOnLine(Peer peer) {
                Log.i("WK", "上线设备-->" + peer.getNickName());
                if (!mPeerList.contains(peer)) {
                    mPeerList.add(peer);
                    mPeerAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onDeviceOffLine(Peer peer) {
                if (mPeerList.contains(peer)) {
                    mPeerList.remove(peer);
                    mPeerAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onAnswerRequestConnect(Peer peer) {
                // 回应对端申请的建立连接的请求
                if (peer.equals(mPeerRequestConn)) {
                    Toast.makeText(mContext, "验证成功,开始建立连接", Toast.LENGTH_SHORT).show();
                    // TODO: 2018/1/14 在这边开始建立Socket连接，并发送文件，切换到文件传输的界面
                    if (mOnPairActionListener != null) {
                        mOnPairActionListener.onPairSuccess(peer);
                    }
                } else {
                    if (mOnPairActionListener != null) {
                        mOnPairActionListener.onPeerPairFailed(peer);
                    }
                    Toast.makeText(mContext, "Peer不匹配，验证失败", Toast.LENGTH_SHORT).show();
                }
                // 将界面切换到文件传输的Fragment，根据peer中的主机地址连接到指定的那个主机
            }
        });
        mPeerManager.startMsgListener();
        requestPermission();

    }

    private void requestPermission() {
        // 扫描附近的热点信号需要获取位置权限
        new RxPermissions(mContext)
                .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(permission -> {
                    if (permission.granted) {
                        ToastUtils.showShort(getActivity(), "位置权限被授予");
                        mHandler.sendEmptyMessage(TYPE_SCAN_WIFI);
                        mScanWifiTimer = new OSTimer(null, () -> mHandler.sendEmptyMessage(TYPE_SCAN_WIFI), 5000, true);
                        mScanWifiTimer.start();
                    } else {
                        ToastUtils.showShort(mContext, "请授予位置权限，否则无法扫描附近的热点！");
                    }
                });
    }

    /**
     * 初始化wifi状态,关闭热点开启wifi
     */
    private void initWifiState() {
        mWifiMgr = WifiMgr.getInstance(mContext);
        if (ApManager.isApOn(mContext)) {
            ApManager.turnOffAp(mContext);
        }
        if (!mWifiMgr.isWifiEnable()) {
            mWifiMgr.openWifi();
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (UiUtils.clickValid()) {
            Peer peer = (Peer) adapter.getItem(position);
            if (peer == null) {
                return;
            }
            // 用户点击的是开启热点的用户
            if (peer.isHotsPot()) {
                if (peer.isAndroidODevice(peer.getSsid())) {
                    Intent intent = new Intent(getContext(), InputHotspotPwdActivity.class);
                    intent.putExtra("ssid", peer.getSsid());
                    startActivityForResult(intent, 1);
                } else {
                    mTvTip.setTextColor(Color.WHITE);
                    mTvTip.setText("正在努力连接到该网络...");
                    //todo 此处连接热点不需要密码，可能会在判断preShareKey为null时出现bug
                    transferFileToPeer(peer);
                    isStopScan = true;
                }
            } else {
                // 点击的是局域网内的用户
                // 需要在peer上加一个标记
                mPeerRequestConn = peer;
                InetAddress dest;
                try {
                    dest = InetAddress.getByName(mPeerRequestConn.getHostAddress());
                    // 将消息发送给对端
                    mPeerManager.send2Peer(generateReqConnMsg(), dest, com.merpyzf.transfermanager.common.Const.UDP_PORT);
                    if (mOnPairActionListener != null) {
                        mOnPairActionListener.onSendConnRequest();
                    }
                    Toast.makeText(mContext, "发送建立请求连接", Toast.LENGTH_SHORT).show();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成请求建立连接的消息
     *
     * @return 转换后的协议字符串
     */
    private String generateReqConnMsg() {
        SignMessage signMessage = new SignMessage();
        signMessage.setHostAddress(NetworkUtil.getLocalIp(mContext));
        signMessage.setNickName(SharedPreUtils.getNickName(mContext));
        signMessage.setAvatarPosition(SharedPreUtils.getAvatar(mContext));
        signMessage.setMsgContent(" ");
        signMessage.setCmd(SignMessage.cmd.REQUEST_CONN);
        return signMessage.convertProtocolStr();
    }

    public void transferFileToPeer(Peer peer) {
        // 传输前建立wifi连接
        connNewWifi(peer.getSsid(), peer.getPreSharedKey());
        // 获取远端建立热点设备的ip地址
        mLocalAddress = WifiMgr.getInstance(mContext).getIpAddressFromHotspot();
        if ("0.0.0.0".equals(mLocalAddress)) {
            SingleThreadPool.getSingleton().execute(() -> {
                // 当连接上wifi后立即获取对端主机地址，有可能获取不到，需要多次获取才能拿到
                int count = 0;
                while (count < com.merpyzf.transfermanager.common.Const.PING_COUNT) {
                    mLocalAddress = WifiMgr.getInstance(mContext).getIpAddressFromHotspot();
                    Message msg = mHandler.obtainMessage();
                    msg.what = TYPE_GET_IP;
                    msg.arg1 = count;
                    mHandler.sendMessage(msg);
                    Log.i(TAG, "第 " + count + " 次尝试获取接收端IP 获取结果->  " + mLocalAddress);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 获取到主机地址，发送消息到Handler进行文件的发送
                    if (!"0.0.0.0".equals(mLocalAddress)) {
                        // 设置主机地址
                        peer.setHostAddress(mLocalAddress);
                        Message message = mHandler.obtainMessage();
                        message.obj = peer;
                        message.what = TYPE_SEND_FILE;
                        mHandler.sendMessage(message);
                        return;
                    }
                    count++;
                }
                // 尝试多次后获取IP失败，通知界面更新UI
                mHandler.sendEmptyMessage(TYPE_GET_IP_FAILED);
            });
        }
    }

    private void connNewWifi(String ssid, String pwd) {

        WifiConfiguration wifiCfg;
        if (null == pwd) {
            wifiCfg = WifiMgr.createWifiCfg(ssid, null, WifiMgr.WIFICIPHER_NOPASS);
        } else {
            wifiCfg = WifiMgr.createWifiCfg(ssid, pwd, WifiMgr.WIFICIPHER_WPA);
        }
        // 连接热点
        mWifiMgr.connectNewWifi(wifiCfg);
    }

    /**
     * 扫描WIFI
     */
    @SuppressLint("CheckResult")
    public void scanWifi() {
        List<ScanResult> scanResults = mWifiMgr.startScan();
        if (null == scanResults) {
            return;
        }
        // 扫描之前先移除上一次扫描到的热点信号
        removeLastScanHotsPot(mPeerList);
        // 从扫描的附近热点中找到可连接的设备
        getPeerFromScanResults(scanResults).subscribe(peer -> {
            mPeerList.add(peer);
            mPeerAdapter.notifyDataSetChanged();
        });
    }

    /**
     * 从扫描到的附近热点中筛选出可连接的对象
     *
     * @param scanResults 扫描到的热点集合
     * @return 可连接热点对象
     */
    private Observable<Peer> getPeerFromScanResults(List<ScanResult> scanResults) {

        return Observable.fromArray(scanResults)
                .flatMap((Function<List<ScanResult>, ObservableSource<ScanResult>>) Observable::fromIterable)
                .filter(scanResult -> scanResult.SSID.startsWith(com.merpyzf.transfermanager.common.Const.HOTSPOT_PREFIX_IDENT)
                        || scanResult.SSID.startsWith(com.merpyzf.transfermanager.common.Const.HOTSPOT_PREFIX_IDENT_O))
                .flatMap((Function<ScanResult, ObservableSource<Peer>>) scanResult -> {
                    String nick = null;
                    int avatarPosition = 0;
                    if (scanResult.SSID.startsWith(com.merpyzf.transfermanager.common.Const.HOTSPOT_PREFIX_IDENT)) {
                        String[] apNickAndAvatar = NetworkUtil.getApNickAndAvatar(scanResult.SSID);
                        avatarPosition = Integer.valueOf(apNickAndAvatar[0]);
                        nick = apNickAndAvatar[1];
                    } else if (scanResult.SSID.startsWith(com.merpyzf.transfermanager.common.Const.HOTSPOT_PREFIX_IDENT_O)) {
                        nick = scanResult.SSID;
                        avatarPosition = Const.AVATAR_LIST.size() - 1;
                    }
                    // todo 主机地址先默认预先设置为未知
                    Peer peer = new Peer();
                    peer.setHostAddress("未知");
                    peer.setSsid(scanResult.SSID);
                    peer.setAvatarPosition(avatarPosition);
                    peer.setNickName(nick);
                    peer.setHotsPot(true);
                    return Observable.just(peer);
                });
    }

    /**
     * 移除列表中上一次扫描的热点
     *
     * @param mPeerList 可连接设备
     */
    private void removeLastScanHotsPot(List<Peer> mPeerList) {
        for (int i = 0; i < mPeerList.size(); i++) {
            if (mPeerList.get(i).isHotsPot()) {
                mPeerList.remove(i);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == InputHotspotPwdActivity.RESULT_GET_HOTSPOT_INFO) {
            String hotspotInfo = data.getStringExtra("hotspot_info");
            ToastUtils.showShort(getContext(), hotspotInfo);
            Peer peer = parseJson2Peer(hotspotInfo);
            transferFileToPeer(peer);
        }

    }

    private Peer parseJson2Peer(String hotspotInfo) {
        Peer peer = new Peer();
        try {
            JSONObject jsonObject = new JSONObject(hotspotInfo);
            peer.setAvatarPosition(Const.AVATAR_LIST.size() - 1);
            peer.setHotsPot(true);
            peer.setNickName((String) jsonObject.get("ssid"));
            peer.setSsid((String) jsonObject.get("ssid"));
            peer.setPreSharedKey((String) jsonObject.get("preSharedKey"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return peer;
    }

    @Override
    public void onDestroy() {
        // 发送下线广播
        mPeerManager.sendOffLineBroadcast();
        // 关闭UdpServer端，停止接收数据
        mPeerManager.stopMsgListener();
        mHandler.removeCallbacks(null);
        super.onDestroy();

    }

    public void setOnPeerActionListener(OnPairActionListener onPairActionListener) {
        this.mOnPairActionListener = onPairActionListener;
    }

    private static class ScanPeerHandler extends Handler {
        private final WeakReference<ScanPeerFragment> mFragment;

        ScanPeerHandler(ScanPeerFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ScanPeerFragment scanPeerFragment = mFragment.get();
            switch (msg.what) {
                case TYPE_SCAN_WIFI:
                    if (!scanPeerFragment.isStopScan) {
                        scanPeerFragment.scanWifi();
                    }
                    break;
                case TYPE_SEND_FILE:
                    Peer peer = (Peer) msg.obj;
                    Log.i("w2k", "准备向 " + peer.getHostAddress() + " 发送文件");
                    // 当前ping的次数
                    final int[] currentPingCount = {0};
                    Observable.interval(0, 500, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    Log.i(TAG, "ping.....");
                                    if (currentPingCount[0] < Const.PING_COUNT) {
                                        scanPeerFragment.mTvTip.setTextColor(Color.WHITE);
                                        scanPeerFragment.mTvTip.setText("正在检查网络连通性...");
                                        if (com.merpyzf.transfermanager.util.NetworkUtil.pingIpAddress(peer.getHostAddress())) {
                                            if (scanPeerFragment.mOnPairActionListener != null) {
                                                // 取消wifi扫描
                                                scanPeerFragment.isStopScan = true;
                                                scanPeerFragment.mOnPairActionListener.onStartTransfer(peer, App.getTransferFileList());
                                                d.dispose();
                                            }
                                        } else {
                                            scanPeerFragment.isStopScan = false;
                                        }
                                    } else {
                                        d.dispose();
                                        // 继续开始扫描附近wifi
                                        scanPeerFragment.isStopScan = false;
                                    }
                                    currentPingCount[0]++;
                                }

                                @Override
                                public void onNext(Long value) {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                    break;

                case TYPE_GET_IP_FAILED:
                    scanPeerFragment.isStopScan = false;
                    scanPeerFragment.mTvTip.setTextColor(Color.RED);
                    scanPeerFragment.mTvTip.setText("获取接收端IP地址失败，请点击好友头像重试...");
                    break;

                case TYPE_GET_IP:
                    int count = msg.arg1;
                    scanPeerFragment.mTvTip.setText("正在第" + count + "次尝试获取接收端IP地址...");
                    break;
                default:
                    break;
            }
        }
    }

}
