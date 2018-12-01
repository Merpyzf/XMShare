package com.merpyzf.xmshare.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.qrcodescan.google.encoding.EncodingHandler;
import com.merpyzf.radarview.RadarLayout;
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
import com.merpyzf.xmshare.receiver.APChangedReceiver;
import com.merpyzf.xmshare.ui.adapter.PeerAdapter;
import com.merpyzf.xmshare.util.DisplayUtils;
import com.merpyzf.xmshare.util.SharedPreUtils;
import com.merpyzf.xmshare.util.ToastUtils;
import com.merpyzf.xmshare.util.UiUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 接收端 - 搜索好友的界面
 * 1 -  局域网内设备发现
 * 2 -  AP热点模式
 */
public class ReceivePeerFragment extends Fragment implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.radar)
    RadarLayout radar;
    @BindView(R.id.rv_peers)
    RecyclerView mRvPeerList;
    @BindView(R.id.tv_tip)
    TextView mTvTip;
    // 切换到热点传输模式
    @BindView(R.id.btn_change_ap)
    Button mBtnChangedAp;
    @BindView(R.id.tv_net_name)
    TextView mTvNetName;
    // 网络模式
    @BindView(R.id.tv_mode)
    TextView mTvNetMode;
    @BindView(R.id.civ_avatar)
    CircleImageView mCivAvatar;
    @BindView(R.id.iv_qrcode)
    ImageView mIvQrCode;
    // 展示二维码相关信息的布局
    @BindView(R.id.rl_show_qrcode_info)
    RelativeLayout mRlShowQrCodeInfo;
    // 热点名称
    @BindView(R.id.tv_hotspot_name_o)
    TextView mTvHotspotNameO;
    // 热点密码
    @BindView(R.id.tv_hotspot_pwd)
    TextView mTvHotspotPwd;
    @BindView(R.id.ll_show_peers_container)
    LinearLayout mLlShowPeersContainer;

    private PeerManager mPeerManager;
    private Context mContext;
    private ArrayList<Peer> mPeerList;
    private Unbinder mUnbinder;

    private OSTimer mOsTimer;
    private PeerAdapter mPeerAdapter;
    private OnReceivePairActionListener mOnReceivePairActionListener;
    private String mNickName;
    private WifiMgr mWifiMgr;
    private APChangedReceiver mApChangedReceiver;
    private OSTimer mHideTipTimer;
    private WifiManager.LocalOnlyHotspotReservation mReservation;
    private static final String TAG = ReceivePeerFragment.class.getSimpleName();
    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;

    public ReceivePeerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_receive_peer, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        this.mContext = getActivity();
        mPeerList = new ArrayList<>();
        initUI();
        initEvent();
        mPeerAdapter = new PeerAdapter(R.layout.item_rv_recive_peer, mPeerList);
        mRvPeerList.setAdapter(mPeerAdapter);
        mPeerAdapter.setOnItemClickListener(this);
        // 传输的方式
        int transferMode = SharedPreUtils.getInteger(mContext, Const.SP_USER, Const.KEY_TRANSFER_MODE,
                Const.TRANSFER_MODE_LAN);
        if (transferMode == Const.TRANSFER_MODE_AP) {
            // 热点传输优先-> 无论是否连接wifi都要建立热点
            requestPermissionAndInitAp();
            mBtnChangedAp.setVisibility(View.INVISIBLE);
        } else if (transferMode == Const.TRANSFER_MODE_LAN) {
            // 局域网传输优先-> 如果没有连接wifi 就开启热点
            mWifiMgr = WifiMgr.getInstance(mContext);
            if (NetworkUtil.isWifi(mContext)) {
                initUdpListener();
                mBtnChangedAp.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(mContext, "建立热点进行传输", Toast.LENGTH_SHORT).show();
                requestPermissionAndInitAp();
                mBtnChangedAp.setVisibility(View.INVISIBLE);
            }
        }
        return rootView;
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mBtnChangedAp.setOnClickListener(v -> {
            if (UiUtils.clickValid()) {
                // 如果当前是网络wifi环境下才可以切换到热点模式进行传输
                if (NetworkUtil.isWifi(mContext)) {
                    // 发送离线通知
                    // 释放UDP局域网内设备发现涉及到的相关资源
                    releaseUdpListener();
                    // 建立AP
                    requestPermissionAndInitAp();
                    // 隐藏切换到热点模式按钮
                    mBtnChangedAp.setVisibility(View.INVISIBLE);
                    Toast.makeText(mContext, "正在拼命开启热点中，请等待...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 初始化一个UDP的监听
     */
    private void initUdpListener() {
        String nickName = com.merpyzf.xmshare.util.SharedPreUtils.getString(mContext, Const.SP_USER, "nickName", "");
        mPeerManager = new PeerManager(mContext, nickName);
        mPeerManager.setOnPeerActionListener(new OnPeerActionListener() {
            @Override
            public void onDeviceOffLine(Peer peer) {
                if (mPeerList.contains(peer)) {
                    mPeerList.remove(peer);
                    Toast.makeText(mContext, "【接收文件】设备离线了 --> " + peer.getHostAddress(), Toast.LENGTH_SHORT).show();
                    mPeerAdapter.notifyDataSetChanged();
                }
                checkIsHide();
            }

            @Override
            public void onRequestConnect(Peer peer) {
                if (!mPeerList.contains(peer)) {
                    mPeerList.add(peer);
                }
                checkIsHide();
                Log.i("w2k", "有设备请求建立连接:" + peer.getNickName() + " " + peer.getHostAddress());
                Toast.makeText(mContext, peer.getNickName() + "请求建立连接", Toast.LENGTH_SHORT).show();
                mPeerAdapter.notifyDataSetChanged();
                // TODO: 2018/1/14  开启Socket服务等待设备连接，并开跳转到文件接收的界面，开始接收文件
                if (mOnReceivePairActionListener != null) {
                    mOnReceivePairActionListener.onRequestSendFileAction();
                }
            }
        });
        mPeerManager.startMsgListener();
        /**
         * 循环间隔一段时间发送一个上线广播
         */
        mOsTimer = mPeerManager.sendOnLineBroadcast(true);
    }


    /**
     * 申请并初始化AP
     */
    private void requestPermissionAndInitAp() {
        // 检查是否具备修改系统设置的权限
        boolean permission = false;
        // 获取当前设备的SDK的版本
        int sdkVersion = Build.VERSION.SDK_INT;
        // 如果
        if (sdkVersion >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(mContext);
        } else {
            permission = ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            closeMobileNetwork();
            // 拥有权限直接建立热点
            initAp();
        } else {
            requestWriteSettings();
        }
    }

    /**
     * 提示用户是否关闭手机数据网络
     */
    private void closeMobileNetwork() {

        //if (NetworkUtil.isMobile(mContext)) {
        //    MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
        //            .title("提示")
        //            .content("开启热点可能会产生流量资费，请选择是否关闭数据流量？")
        //            .positiveText("果断关闭")
        //            .negativeText("保持开启")
        //            .canceledOnTouchOutside(false)
        //            .onPositive((dialog, which) -> {
        //                // TODO: 2018/8/14 关闭移动网络的方法未实现
        //            });
        //    MaterialDialog dialog = builder.build();
        //    dialog.show();
        //}
    }

    // TODO: 2018/8/7 fix: 缩短方法行数

    /**
     * 初始化热点
     */
    @SuppressLint("CheckResult")
    private void initAp() {
        // 如果热点处于开启状态就将其关闭
        if (ApManager.isApOn(mContext)) {
            ApManager.turnOffAp(mContext);
        }
        //通过广播监听热点变化
        mApChangedReceiver = new APChangedReceiver() {
            @Override
            public void onApEnableAction() {

                mLlShowPeersContainer.setVisibility(View.INVISIBLE);
                String apSSID = ApManager.getApSSID(mContext);
                mTvNetName.setText(apSSID);

                if (mOnReceivePairActionListener != null) {
                    mOnReceivePairActionListener.onApEnableAction();
                }

            }

            @Override
            public void onApDisAbleAction() {
                // 热点被关闭的回调方法
                mLlShowPeersContainer.setVisibility(View.VISIBLE);
            }
        };

        IntentFilter intentFilter = new IntentFilter(APChangedReceiver.ACTION_WIFI_AP_STATE_CHANGED);

        mContext.registerReceiver(mApChangedReceiver, intentFilter);


        // 开启热点
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 申请位置权限
            new RxPermissions(getActivity())
                    .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(permission -> {
                        if (permission.granted) {
                            ApManager.configApStateOnAndroidO(getContext(), new ApManager.HotspotStateCallback() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                                    radar.setVisibility(View.INVISIBLE);
                                    String ssid = reservation.getWifiConfiguration().SSID;
                                    String preSharedKey = reservation.getWifiConfiguration().preSharedKey;
                                    App.setReservation(reservation);
                                    radar.setVisibility(View.INVISIBLE);
                                    mRlShowQrCodeInfo.setVisibility(View.VISIBLE);
                                    mTvHotspotNameO.setText(ssid);
                                    mTvHotspotPwd.setText(preSharedKey);
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("ssid", reservation.getWifiConfiguration().SSID);
                                        jsonObject.put("preSharedKey", reservation.getWifiConfiguration().preSharedKey);
                                        String hotspotInfo = jsonObject.toString();
                                        Bitmap bmpLogo = BitmapFactory.decodeResource(getResources(), SharedPreUtils.getAvatar(mContext));
                                        Bitmap qrCode = EncodingHandler.createQRCode(hotspotInfo, DisplayUtils.dip2px(mContext, 200), DisplayUtils.dip2px(mContext, 200), bmpLogo);
                                        mIvQrCode.setImageBitmap(qrCode);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onStopped() {
                                }

                                @Override
                                public void onFailed(int reason) {
                                }
                            });
                        }
                    });
        } else {
            ApManager.configApState(mContext, SharedPreUtils.getNickName(mContext), SharedPreUtils.getAvatar(mContext));
            mRlShowQrCodeInfo.setVisibility(View.INVISIBLE);
            radar.setVisibility(View.VISIBLE);
        }
    }

    private void checkIsHide() {
        if (mTvTip == null) {
            return;
        }
        if (mPeerList.size() > 0) {
            mTvTip.setVisibility(View.VISIBLE);
            UiUtils.delayHideView(getActivity(), mTvTip, 3 * 1000);
        } else {
            mTvTip.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 初始化UI
     */
    private void initUI() {

        Glide.with(mContext)
                .load(Const.AVATAR_LIST.get(SharedPreUtils.getAvatar(mContext)))
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mCivAvatar);

        if (NetworkUtil.isWifi(mContext)) {
            WifiInfo currConnWifiInfo = WifiMgr.getInstance(mContext).getCurrConnWifiInfo();
            String ssid = currConnWifiInfo.getSSID();
            mTvNetName.setText(ssid);
            mTvNetMode.setVisibility(View.VISIBLE);
            // 显示三秒钟后隐藏当前网络模式信息
            UiUtils.delayHideView(getActivity(), mTvNetMode, 3 * 1000);
        } else {
            mTvNetMode.setVisibility(View.INVISIBLE);
        }

        radar.setDuration(2000);
        radar.setStyleIsFILL(false);
        radar.setRadarColor(Color.WHITE);
        radar.start();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvPeerList.setLayoutManager(linearLayoutManager);
        mTvTip.setVisibility(View.INVISIBLE);

    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        // 防抖动
        if (UiUtils.clickValid()) {
            // 发送同意对端发送文件的回应
            Peer peer = (Peer) adapter.getItem(position);
            SignMessage signMessage = new SignMessage();
            signMessage.setHostAddress(NetworkUtil.getLocalIp(mContext));
            signMessage.setCmd(SignMessage.Cmd.ANSWER_REQUEST_CONN);
            signMessage.setMsgContent(" ");
            signMessage.setNickName(SharedPreUtils.getNickName(mContext));
            signMessage.setAvatarPosition(SharedPreUtils.getAvatar(mContext));
            String protocolStr = signMessage.convertProtocolStr();
            try {
                InetAddress dest = InetAddress.getByName(peer.getHostAddress());
                mPeerManager.send2Peer(protocolStr, dest, com.merpyzf.transfermanager.common.Const.UDP_PORT);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        if (mHideTipTimer != null) {
            mHideTipTimer.cancel();
            mHideTipTimer = null;
        }
        releaseUdpListener();
        if (mApChangedReceiver != null) {
            mContext.unregisterReceiver(mApChangedReceiver);
        }
        super.onDestroy();
    }

    /**
     * 释放UdpServer占用的资源
     */
    private void releaseUdpListener() {

        if (mPeerManager != null) {
            /**
             * 发送下线广播
             */
            mPeerManager.sendOffLineBroadcast();
            mPeerManager = null;
        }

        if (mOsTimer != null) {
            mOsTimer.cancel();
            mOsTimer = null;
        }
        if (mPeerManager != null) {
            mPeerManager.stopMsgListener();
            mPeerManager = null;
        }

    }

    public interface OnReceivePairActionListener {

        /**
         * 请求发送文件(提前建立ServerSocket)
         */
        void onRequestSendFileAction();

        /**
         * AP建立成功的回调
         */
        void onApEnableAction();


    }

    public void setOnReceivePairActionListener(OnReceivePairActionListener onReceivePairActionListener) {
        this.mOnReceivePairActionListener = onReceivePairActionListener;
    }

    private void requestWriteSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(getActivity())) {
                initAp();
            } else {
                ToastUtils.showShort(getContext(), "授权之后才能开启热点");
            }
        }
    }
}
