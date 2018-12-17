package com.merpyzf.xmshare.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.merpyzf.common.widget.RadarLayout;
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
import com.merpyzf.xmshare.receiver.APChangedReceiver;
import com.merpyzf.xmshare.ui.adapter.PeerAdapter;
import com.merpyzf.common.utils.DisplayUtils;
import com.merpyzf.common.utils.PersonalSettingUtils;
import com.merpyzf.common.utils.ToastUtils;
import com.merpyzf.xmshare.util.UiUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 接收端 - 搜索好友的界面
 * 1 -  局域网内设备发现
 * 2 -  AP热点模式
 *
 * @author wangke
 */
public class ReceivePeerFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.radar)
    RadarLayout mRadar;
    @BindView(R.id.tv_nickname)
    TextView mTvNickName;
    @BindView(R.id.rv_peers)
    RecyclerView mRvPeerList;
    @BindView(R.id.tv_tip)
    TextView mTvTip;
    @BindView(R.id.btn_change_ap)
    Button mBtnChangedAp;
    @BindView(R.id.tv_net_name)
    TextView mTvNetName;
    @BindView(R.id.tv_mode)
    TextView mTvNetMode;
    @BindView(R.id.civ_avatar)
    CircleImageView mCivAvatar;
    @BindView(R.id.iv_qrcode)
    ImageView mIvQrCode;
    @BindView(R.id.rl_show_qrcode_info)
    RelativeLayout mRlShowQrCodeInfo;
    @BindView(R.id.tv_hotspot_name_o)
    TextView mTvHotspotNameO;
    @BindView(R.id.tv_hotspot_pwd)
    TextView mTvHotspotPwd;
    @BindView(R.id.ll_show_peers_container)
    LinearLayout mLlShowPeersContainer;
    @BindView(R.id.iv_retry)
    ImageView mIvRetry;
    @BindView(R.id.tv_top_tip)
    TextView mTvTopTip;
    private PeerManager mPeerManager;
    private ArrayList<Peer> mPeerList = new ArrayList<>();
    private TimerHelper mTimerHelper;
    private PeerAdapter mPeerAdapter;
    private OnReceivePairActionListener mOnReceivePairActionListener;
    private APChangedReceiver mApChangedReceiver;
    private static final String TAG = ReceivePeerFragment.class.getSimpleName();
    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;

    public ReceivePeerFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_receive_peer;
    }

    @Override
    protected void doCreateView(View rootView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvPeerList.setLayoutManager(linearLayoutManager);
        mPeerAdapter = new PeerAdapter(R.layout.item_rv_recive_peer, mPeerList);
        mRvPeerList.setAdapter(mPeerAdapter);
        updateTipState();
        setUserInfo();
        initTransferNetMode();
        showCurrNetInfo();
        startRadarAnimation();
    }

    private void startRadarAnimation() {
        mRadar.setDuration(2000);
        mRadar.setStyleIsFILL(false);
        mRadar.setRadarColor(Color.WHITE);
        mRadar.start();
    }

    private void showCurrNetInfo() {
        if (NetworkUtil.isWifi(mContext)) {
            WifiInfo currConnWifiInfo = WifiHelper.getInstance(mContext).getCurrConnWifiInfo();
            String netName = currConnWifiInfo.getSSID();
            mTvNetName.setText(netName);
            mTvNetMode.setVisibility(View.VISIBLE);
            UiUtils.delayHideView(mTvNetMode, 3 * 1000);
        } else {
            mTvNetMode.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void doCreateEvent() {
        mPeerAdapter.setOnItemClickListener(this);
        mBtnChangedAp.setOnClickListener(v -> {
            if (UiUtils.clickValid()) {
                // 如果当前是网络wifi环境下才可以切换到热点模式进行传输
                if (NetworkUtil.isWifi(mContext)) {
                    stopPeerActionListener();
                    stopSendOnlineBroadcast();
                    sendOfflineBroadcast();
                    // 建立AP
                    requestPermissionAndInitAp();
                    // 隐藏切换到热点模式按钮
                    mBtnChangedAp.setVisibility(View.INVISIBLE);
                    Toast.makeText(mContext, "正在拼命开启热点...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mIvRetry.setOnClickListener(v -> {
            requestPermissionAndInitAp();
            mIvRetry.setVisibility(View.INVISIBLE);
            Toast.makeText(mContext, "正在拼命开启热点...", Toast.LENGTH_SHORT).show();
        });
    }

    private void initTransferNetMode() {
        int transferMode = PersonalSettingUtils.getTransferMode(mContext);
        if (transferMode == PersonalSettingUtils.TRANSFER_MODE_AP) {
            // 热点传输优先-> 无论是否连接wifi都要建立热点
            requestPermissionAndInitAp();
            mBtnChangedAp.setVisibility(View.INVISIBLE);
        } else if (transferMode == PersonalSettingUtils.TRANSFER_MODE_LAN) {
            // 局域网传输优先-> 如果没有连接wifi 就开启热点
            if (NetworkUtil.isWifi(mContext)) {
                startPeerActionListener();
                mTimerHelper = mPeerManager.sendOnLineBroadcast(true);
                mBtnChangedAp.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(mContext, "建立热点进行传输", Toast.LENGTH_SHORT).show();
                requestPermissionAndInitAp();
                mBtnChangedAp.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 初始化一个UDP的监听
     */
    private void startPeerActionListener() {
        mPeerManager = new PeerManager(mContext);
        mPeerManager.setOnPeerActionListener(new OnPeerActionListener() {
            @Override
            public void onDeviceOffLine(Peer peer) {
                if (mPeerList.contains(peer)) {
                    mPeerList.remove(peer);
                    mPeerAdapter.notifyDataSetChanged();
                }
                updateTipState();
            }

            @Override
            public void onRequestConnect(Peer peer) {
                if (!mPeerList.contains(peer)) {
                    mPeerList.add(peer);
                }
                updateTipState();
                Log.i("w2k", "有设备请求建立连接:" + peer.getNickName() + " " + peer.getHostAddress());
                Toast.makeText(mContext, peer.getNickName() + "请求建立连接", Toast.LENGTH_SHORT).show();
                mPeerAdapter.notifyDataSetChanged();
                if (mOnReceivePairActionListener != null) {
                    mOnReceivePairActionListener.onRequestSendFileAction();
                }
            }
        });
        mPeerManager.startMsgListener();
    }


    /**
     * 申请并初始化AP
     */
    private void requestPermissionAndInitAp() {
        if (hasWriteSystemPermission()) {
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
    }

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
                mIvRetry.setVisibility(View.INVISIBLE);
                mTvTopTip.setText("正在等待发送端连接...");
                mLlShowPeersContainer.setVisibility(View.INVISIBLE);
                String apNetName = ApManager.getApSSID(mContext);
                mTvNetName.setText(apNetName);
                if (mOnReceivePairActionListener != null) {
                    mOnReceivePairActionListener.onApEnableAction();
                }
            }

            @Override
            public void onApDisAbleAction() {
                // 热点被关闭的回调方法
                mLlShowPeersContainer.setVisibility(View.VISIBLE);
                mRlShowQrCodeInfo.setVisibility(View.INVISIBLE);
                mRadar.setVisibility(View.INVISIBLE);
                mIvRetry.setVisibility(View.VISIBLE);
                mTvTopTip.setText("热点已被系统自动关闭，点击重新创建...");
                ToastUtils.showShort(mContext, "热点被关闭");
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
                                    hideRadarLayout();
                                    String ssid = reservation.getWifiConfiguration().SSID;
                                    String preSharedKey = reservation.getWifiConfiguration().preSharedKey;
                                    App.setReservation(reservation);
                                    mRlShowQrCodeInfo.setVisibility(View.VISIBLE);
                                    mTvHotspotNameO.setText(ssid);
                                    mTvHotspotPwd.setText(preSharedKey);
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("ssid", reservation.getWifiConfiguration().SSID);
                                        jsonObject.put("preSharedKey", reservation.getWifiConfiguration().preSharedKey);
                                        String hotspotInfo = jsonObject.toString();
                                        Bitmap qrCode = EncodingHandler.createQRCode(hotspotInfo, DisplayUtils.dip2px(mContext, 200));
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
            ApManager.configApState(mContext, PersonalSettingUtils.getNickname(mContext),
                    PersonalSettingUtils.getAvatar(mContext));
            mRlShowQrCodeInfo.setVisibility(View.INVISIBLE);
            mRadar.setVisibility(View.VISIBLE);
        }
    }

    private void hideRadarLayout() {
        mRadar.stop();
        mRadar.setVisibility(View.INVISIBLE);

    }

    private void updateTipState() {
        if (mTvTip == null) {
            return;
        }
        if (mPeerList.size() > 0) {
            mTvTip.setVisibility(View.VISIBLE);
        } else {
            mTvTip.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (UiUtils.clickValid()) {
            // 回应对端，同意发送文件
            Peer peer = (Peer) adapter.getItem(position);
            SignMessage signMessage = createSignMessage(SignMessage.CMD.ANSWER_REQUEST_CONN);
            String protocolStr = signMessage.convertProtocolStr();
            try {
                InetAddress dest = InetAddress.getByName(peer.getHostAddress());
                mPeerManager.sendMsgToPeer(protocolStr, dest, com.merpyzf.transfermanager.common.Const.UDP_PORT);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
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


    private boolean hasWriteSystemPermission() {
        // 检查是否具备修改系统设置的权限
        boolean hasPermission;
        int sdkVersion = Build.VERSION.SDK_INT;
        // 如果
        if (sdkVersion >= Build.VERSION_CODES.M) {
            hasPermission = Settings.System.canWrite(mContext);
        } else {
            hasPermission = hasPermission(mContext, Manifest.permission.WRITE_SETTINGS);
        }
        return hasPermission;
    }

    public static boolean hasPermission(Context context, String permission) {
        int perm = context.checkCallingOrSelfPermission(permission);
        return perm == PackageManager.PERMISSION_GRANTED;
    }


    public void setOnReceivePairActionListener(OnReceivePairActionListener onReceivePairActionListener) {
        this.mOnReceivePairActionListener = onReceivePairActionListener;
    }

    private void requestWriteSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (hasWriteSystemPermission()) {
                initAp();
            } else {
                ToastUtils.showShort(getContext(), "授权之后才能开启热点");
            }

        }
    }

    @Override
    public void onDestroy() {
        stopPeerActionListener();
        stopSendOnlineBroadcast();
        sendOfflineBroadcast();
        if (mApChangedReceiver != null) {
            mContext.unregisterReceiver(mApChangedReceiver);
        }
        super.onDestroy();
    }

    private void sendOfflineBroadcast() {
        if (mPeerManager != null) {
            mPeerManager.sendOffLineBroadcast();
        }
    }

    private void stopSendOnlineBroadcast() {
        if (mPeerManager != null) {
            mTimerHelper.cancel();
        }
    }

    private void stopPeerActionListener() {
        if (mPeerManager != null) {
            mPeerManager.stopMsgListener();
        }
    }

    private SignMessage createSignMessage(int cmd) {
        SignMessage signMessage = new SignMessage();
        signMessage.setHostAddress(NetworkUtil.getLocalIp(mContext));
        signMessage.setCmd(cmd);
        signMessage.setNickName(PersonalSettingUtils.getNickname(mContext));
        signMessage.setAvatarPosition(PersonalSettingUtils.getAvatar(mContext));
        return signMessage;
    }

    private void setUserInfo() {
        int avatar = PersonalSettingUtils.getAvatar(mContext);
        Glide.with(mContext)
                .load(Const.AVATAR_LIST.get(avatar))
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mCivAvatar);
        String nickname = PersonalSettingUtils.getNickname(mContext);
        mTvNickName.setText(nickname);

    }

}
