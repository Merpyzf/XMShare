package com.merpyzf.xmshare.ui.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.Peer;
import com.merpyzf.transfermanager.send.SenderManager;
import com.merpyzf.transfermanager.util.ApManager;
import com.merpyzf.transfermanager.util.NetworkUtil;
import com.merpyzf.transfermanager.util.WifiMgr;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.receiver.APChangedReceiver;
import com.merpyzf.xmshare.receiver.WifiChangedReceiver;
import com.merpyzf.xmshare.ui.adapter.PeerAdapter;
import com.merpyzf.xmshare.util.SingleThreadPool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 用于测试WIFI的连接
 */
public class HostActivity extends AppCompatActivity {

    private Context mContext;
    private WifiChangedReceiver mWifiChangedReceiver;
    private static final String TAG = HostActivity.class.getSimpleName();
    // 扫描WIFI
    private static final int TYPE_SCAN_WIFI = 1;
    // 发送文件
    private static final int TYPE_SEND_FILE = 2;
    private WifiMgr mWifiMgr;
    private Unbinder mUbbinder;
    private Unbinder mUbinder;
    private List<Peer> mPeerLists;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private int mCountPing = 0;

    // TODO: 2018/8/7 fix: 替换掉这种写法，可能会发生内存泄露
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TYPE_SCAN_WIFI:
                    scanWifi();
                    break;
                case TYPE_SEND_FILE:
                    Peer peer = (Peer) msg.obj;
                    Log.i("w2k", "向 " + peer.getHostAddress() + " 发送文件");
                    SenderManager senderManager = SenderManager.getInstance(mContext);
                    if (mCountPing < Const.PING_COUNT) {
                        if (NetworkUtil.pingIpAddress(peer.getHostAddress())) {
                            // 发送文件
                            senderManager.send(peer.getHostAddress(), App.getSendFileList());
                            break;
                        }
                        Log.i("w2k", peer.getHostAddress() + " ping...");
                        mCountPing++;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // wifi已经建立连接，但是局域网不通
                    if (mCountPing == Const.PING_COUNT) {
                        Toast.makeText(mContext, "网络未连通通，请点击好友头像重试！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private APChangedReceiver mApChangedReceiver;
    private String localAddress;
    private PeerAdapter mPeerAdapter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, HostActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_host);
        mUbinder = ButterKnife.bind(this);
        mWifiMgr = WifiMgr.getInstance(mContext);
        mPeerLists = new ArrayList<>();
        initUI();
        initWifi();
        mPeerAdapter = new PeerAdapter(R.layout.item_rv_send_peer, mPeerLists);
        recyclerView.setAdapter(mPeerAdapter);
        initEvent();

    }

    /**
     * 初始化UI
     */
    private void initUI() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    /**
     * 初始化事件
     */
    private void initEvent() {

        mPeerAdapter.setOnItemClickListener((adapter, view, position) -> {

            // 连接到指定的热点
            connectWifiAndTransfer(mPeerLists.get(position));

        });

    }

    /**
     * 初始化WIFI 关闭AP开启WIFI
     */
    public void initWifi() {

        // 热点不可用被关闭时候的回调
        mApChangedReceiver = new APChangedReceiver() {
            @Override
            public void onApEnableAction() {

            }

            @Override
            public void onApDisAbleAction() {
                // 热点不可用被关闭时候的回调
                mWifiMgr.openWifi();
                Log.i(TAG, "热点被关闭了，等待三秒钟扫描附近的wifi");
                Message message = mHandler.obtainMessage(TYPE_SCAN_WIFI);
                mHandler.sendMessageDelayed(message, 3000);
            }

        };

        IntentFilter intentFilter = new IntentFilter(APChangedReceiver.ACTION_WIFI_AP_STATE_CHANGED);
        registerReceiver(mApChangedReceiver, intentFilter);


        if (!mWifiMgr.isWifiEnable()) {

            if (ApManager.isApOn(mContext)) {
                // wifi关闭 && 热点开启:
                // 先关闭热点再开启Wifi
                ApManager.turnOffAp(mContext);

            } else {

                // wifi关闭 && 热点关闭
                // 直接开启wifi
                mWifiMgr.openWifi(); // wifi开启需要等待一段时间，因此延时1秒中进行局域网内的wifi热点的扫描
                Log.i(TAG, "wifi关闭，热点也是关闭状态,等待1秒钟扫描WIFI");
                Message message = mHandler.obtainMessage(TYPE_SCAN_WIFI);
                mHandler.sendMessageDelayed(message, 1000);
            }

        } else {
            Log.i(TAG, "wifi已开启，直接扫描");
            Message message = mHandler.obtainMessage(TYPE_SCAN_WIFI);
            mHandler.sendMessage(message);
        }
    }


    /**
     * 扫描WIFI
     */
    public void scanWifi() {

        Log.i(TAG, "扫描附近的wifi。。。");

        mWifiMgr.startScan();

        List<ScanResult> scanResults = mWifiMgr.getScanResults();

        if (scanResults == null) {
            return;
        }

        for (ScanResult scanResult : scanResults) {

            if (scanResult.SSID.startsWith("XM")) {

                Log.i("w2k", "符合要求的WIFI SSID -> " + scanResult.SSID);
                String[] apNickAndAvatar = NetworkUtil.getApNickAndAvatar(scanResult.SSID);

                Peer peer = new Peer();
                peer.setHostAddress("未知");
                peer.setSsid(scanResult.SSID);
                // 设置用户名
                peer.setNickName(apNickAndAvatar[1]);

                mPeerLists.add(peer);

                Log.i("w2k", "头像 -> " + apNickAndAvatar[0]);
                Log.i("w2k", "用户名 -> " + apNickAndAvatar[1]);

            }
        }
        mPeerAdapter.notifyDataSetChanged();

    }

    /**
     * 连接wifi,并开始传输文件
     */
    public void connectWifiAndTransfer(Peer peer) {

        if (peer.getSsid().contains("XM")) {

            Log.i("w2k", "连接wifi " + peer.getSsid());
            WifiConfiguration wifiCfg = WifiMgr.createWifiCfg(peer.getSsid(), null, WifiMgr.WIFICIPHER_NOPASS);
            // 连接没有密码的热点
            mWifiMgr.connectNewWifi(wifiCfg);
            // 获取远端建立热点设备的ip地址
            String ipAddressFromHotspot = WifiMgr.getInstance(mContext).getIpAddressFromHotspot();

            Log.i("w2k", "尝试第一次获取接收端的IP地址: " + ipAddressFromHotspot);
            localAddress = WifiMgr.getInstance(mContext).getIpAddressFromHotspot();
            // TODO: 2018/8/7 fix: 重写此处的线程池操作
            SingleThreadPool.getSingleton().execute(() -> {
                // 当连接上wifi后立即获取对端主机地址，有可能获取不到，需要多次获取才能拿到
                int count = 0;
                while (count < Const.PING_COUNT) {
                    localAddress = WifiMgr.getInstance(mContext).getIpAddressFromHotspot();
                    Log.i(TAG, "第 " + count + " 次尝试获取接收端IP 获取结果->  " + localAddress);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 获取到主机地址，发送消息到Handler进行文件的发送
                    if (!"0.0.0.0".equals(localAddress)) {
                        // 设置主机地址
                        peer.setHostAddress(localAddress);
                        Message message = mHandler.obtainMessage();
                        message.obj = peer;
                        message.what = TYPE_SEND_FILE;
                        mHandler.sendMessage(message);
                        break;
                    }

                    count++;
                }


                Log.i("w2k", "receiver get local Ip ----->>>" + localAddress);

            });


        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mApChangedReceiver);
        super.onDestroy();
    }
}
