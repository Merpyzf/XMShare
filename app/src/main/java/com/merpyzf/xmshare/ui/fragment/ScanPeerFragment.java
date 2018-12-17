package com.merpyzf.xmshare.ui.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.transfermanager.PeerManager;
import com.merpyzf.transfermanager.entity.Peer;
import com.merpyzf.transfermanager.entity.SignMessage;
import com.merpyzf.transfermanager.interfaces.OnPeerActionListener;
import com.merpyzf.common.utils.ApManager;
import com.merpyzf.common.utils.NetworkUtil;
import com.merpyzf.common.helper.WifiHelper;
import com.merpyzf.common.helper.TimerHelper;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.receiver.WifiChangedReceiver;
import com.merpyzf.xmshare.ui.adapter.PeerAdapter;
import com.merpyzf.xmshare.ui.activity.InputHotspotPwdActivity;
import com.merpyzf.xmshare.ui.interfaces.OnPairActionListener;
import com.merpyzf.common.utils.PersonalSettingUtils;
import com.merpyzf.common.utils.ToastUtils;
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
    private WifiHelper mWifiHelper;
    private String mLocalAddress;
    private ScanPeerHandler mHandler;
    private TimerHelper mScanWifiTimer;
    private boolean isStopScan;
    private boolean isClickToConnect = false;
    private static final int TYPE_SCAN_WIFI = 1;
    private static final int TYPE_SEND_FILE = 2;
    private static final int TYPE_GET_IP = 3;
    private static final int TYPE_GET_IP_FAILED = 4;
    private static final int TYPE_STOP_GET_IP = 5;
    private static final String TAG = ScanPeerFragment.class.getSimpleName();
    private Disposable mDisposable;
    private WifiChangedReceiver wifiChangedReceiver;

    public ScanPeerFragment() {
        isStopScan = false;
    }


    @Override
    protected void doCreateView(View rootView) {
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
    protected void doCreateEvent() {
        initWifiState();
        mPeerAdapter.setOnItemClickListener(this);
        mHandler = new ScanPeerHandler(this);
        requestPermission();
        startPeerActionListener();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiChangedReceiver = new WifiChangedReceiver() {
            @Override
            public void onWifiConnectedAction() {
                if (isClickToConnect) {
                    isClickToConnect = false;
                    ToastUtils.showShort(mContext, "连接上wifi了,并且要开始传输文件了");
                    transferFileToPeer();
                }
            }

            @Override
            public void onWifiDisConnectedAction() {
                ToastUtils.showShort(mContext, "连接被断开了");
            }
        };
        mContext.registerReceiver(wifiChangedReceiver, filter);
    }

    private void startPeerActionListener() {
        mPeerManager = new PeerManager(mContext);
        mPeerManager.startMsgListener();
        mPeerManager.setOnPeerActionListener(new OnPeerActionListener() {
            @Override
            public void onDeviceOnLine(Peer peer) {
                if (!mPeerList.contains(peer)) {
                    mPeerList.add(peer);
                    mPeerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onDeviceOffLine(Peer peer) {
                if (mPeerList.contains(peer)) {
                    mOnPairActionListener.onOffline(peer);
                    mPeerList.remove(peer);
                    mPeerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onAnswerRequestConnect(Peer peer) {
                if (peer.equals(mPeerRequestConn)) {
                    if (mOnPairActionListener != null) {
                        mOnPairActionListener.onPairSuccess(peer);
                    }
                } else {
                    if (mOnPairActionListener != null) {
                        mOnPairActionListener.onPeerPairFailed(peer);
                    }
                }
            }
        });
    }


    private void stopPeerActionListener() {
        if (mPeerManager != null) {
            mPeerManager.stopMsgListener();
        }
    }

    @SuppressLint("CheckResult")
    private void requestPermission() {
        new RxPermissions(mContext)
                .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(permission -> {
                    if (permission.granted) {
                        ToastUtils.showShort(getActivity(), "位置权限被授予");
                        mHandler.sendEmptyMessage(TYPE_SCAN_WIFI);
                        mScanWifiTimer = new TimerHelper(null, () -> mHandler.sendEmptyMessage(TYPE_SCAN_WIFI), 5000, true);
                        mScanWifiTimer.start();
                    } else {
                        ToastUtils.showShort(mContext, "请授予位置权限，否则无法扫描附近的热点！");
                    }
                });
    }

    /**
     * 初始化wifi状态
     */
    private void initWifiState() {
        mWifiHelper = WifiHelper.getInstance(mContext);
        if (ApManager.isApOn(mContext)) {
            ApManager.turnOffAp(mContext);
        }
        if (!mWifiHelper.isWifiEnable()) {
            mWifiHelper.openWifi();
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (UiUtils.clickValid()) {
            Peer peer = (Peer) adapter.getItem(position);
            if (peer == null) {
                return;
            }

            if (peer.isHotsPot()) {
                isClickToConnect = true;
                if (peer.isAndroidODevice(peer.getSsid())) {
                    Intent intent = new Intent(getContext(), InputHotspotPwdActivity.class);
                    intent.putExtra("ssid", peer.getSsid());
                    startActivityForResult(intent, 1);
                } else {
                    if (!isTryGetIp()) {
                        mTvTip.setTextColor(Color.WHITE);
                        mTvTip.setText("正在努力连接到该网络...");
                        connNewWifi(peer.getSsid(), peer.getPreSharedKey());
                        isStopScan = true;
                    }
                }
            } else {
                mPeerRequestConn = peer;
                InetAddress dest;
                try {
                    dest = InetAddress.getByName(mPeerRequestConn.getHostAddress());
                    SignMessage signMessage = createSignMessage(SignMessage.CMD.REQUEST_CONN);
                    mPeerManager.sendMsgToPeer(signMessage.convertProtocolStr(), dest,
                            com.merpyzf.transfermanager.common.Const.UDP_PORT);
                    if (mOnPairActionListener != null) {
                        mOnPairActionListener.onSendConnRequest();
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isTryGetIp() {
        if (mDisposable == null) {
            return false;
        } else {
            if (mDisposable.isDisposed()) {
                return false;
            } else {
                return true;
            }
        }
    }

    private SignMessage createSignMessage(int cmd) {
        SignMessage signMessage = new SignMessage();
        signMessage.setHostAddress(NetworkUtil.getLocalIp(mContext));
        signMessage.setNickName(PersonalSettingUtils.getNickname(mContext));
        signMessage.setAvatarPosition(PersonalSettingUtils.getAvatar(mContext));
        signMessage.setCmd(cmd);
        return signMessage;
    }


    @SuppressLint("CheckResult")
    public void transferFileToPeer() {
        mDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(aLong -> aLong + 1)
                .subscribe(aLong -> {
                    if (aLong > com.merpyzf.transfermanager.common.Const.PING_COUNT) {
                        mHandler.sendEmptyMessage(TYPE_GET_IP_FAILED);
                        mDisposable.dispose();
                    } else {
                        mLocalAddress = WifiHelper.getInstance(mContext).getIpAddressFromHotspot();
                        Log.i("ww2k", "mLocalAddress-> " + mLocalAddress);
                        Message msg = mHandler.obtainMessage();
                        msg.what = TYPE_GET_IP;
                        msg.arg1 = new Long(aLong).intValue();
                        mHandler.sendMessage(msg);
                        // 获取到主机地址，发送消息到Handler进行文件的发送
                        if (!"0.0.0.0".equals(mLocalAddress)) {
                            Peer peer = new Peer();
                            peer.setHostAddress(mLocalAddress);
                            Message message = mHandler.obtainMessage();
                            message.obj = peer;
                            message.what = TYPE_SEND_FILE;
                            mHandler.sendMessage(message);
                            mDisposable.dispose();
                        }
                    }
                });
    }

    private void connNewWifi(String ssid, String pwd) {
        WifiConfiguration wifiCfg;
        if (null == pwd) {
            wifiCfg = WifiHelper.createWifiCfg(ssid, null, WifiHelper.WIFICIPHER_NOPASS);
        } else {
            wifiCfg = WifiHelper.createWifiCfg(ssid, pwd, WifiHelper.WIFICIPHER_WPA);
        }
        mWifiHelper.connectNewWifi(wifiCfg);
    }

    @SuppressLint("CheckResult")
    public void scanWifi() {
        List<ScanResult> scanResults = mWifiHelper.startScan();
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
            connNewWifi(peer.getSsid(), peer.getPreSharedKey());
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

    public void setOnPeerActionListener(OnPairActionListener onPairActionListener) {
        this.mOnPairActionListener = onPairActionListener;
    }

    // TODO: 2018/11/28 存在内存泄露从而导致的空指针异常
    private static class ScanPeerHandler extends Handler {
        private final WeakReference<ScanPeerFragment> fragment;
        private Disposable disposable;

        ScanPeerHandler(ScanPeerFragment fragment) {
            this.fragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ScanPeerFragment scanPeerFragment = fragment.get();
            switch (msg.what) {
                case TYPE_SCAN_WIFI:
                    if (!scanPeerFragment.isStopScan) {
                        scanPeerFragment.scanWifi();
                    }
                    break;
                case TYPE_SEND_FILE:
                    Peer peer = (Peer) msg.obj;
                    // 当前ping的次数
                    final int[] currentPingCount = {0};
                    Observable.interval(0, 500, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Long>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    disposable = d;
                                }

                                @Override
                                public void onNext(Long value) {
                                    if (currentPingCount[0] < Const.PING_COUNT) {
                                        scanPeerFragment.mTvTip.setTextColor(Color.WHITE);
                                        scanPeerFragment.mTvTip.setText("正在检查网络连通性...");
                                        if (NetworkUtil.pingIpAddress(peer.getHostAddress())) {
                                            if (scanPeerFragment.mOnPairActionListener != null) {
                                                // 取消wifi扫描
                                                scanPeerFragment.isStopScan = true;
                                                scanPeerFragment.mOnPairActionListener.onStartTransfer(peer, App.getTransferFileList());
                                                disposable.dispose();
                                            }
                                        } else {
                                            scanPeerFragment.isStopScan = false;
                                        }
                                    } else {
                                        disposable.dispose();
                                        // 继续开始扫描附近wifi
                                        scanPeerFragment.isStopScan = false;
                                    }
                                    currentPingCount[0]++;
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

                case TYPE_STOP_GET_IP:
                    Log.i("ww2k", "收到停止获取ip的action了");
                    if (disposable != null) {
                        Log.i("ww2k", "isDisposed-> " + disposable.isDisposed());
                        if (!disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        stopGetIp();
        stopPeerActionListener();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        if (mPeerManager != null) {
            mPeerManager.sendOffLineBroadcast();
            mPeerManager = null;
        }
        mContext.unregisterReceiver(wifiChangedReceiver);
        super.onDestroy();

    }

    private void stopGetIp() {
        if (mHandler != null) {
            Message message = mHandler.obtainMessage();
            message.what = TYPE_STOP_GET_IP;
            mHandler.sendMessage(message);
            mHandler.removeCallbacks(null);
        }
    }
}
