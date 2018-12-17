package com.merpyzf.xmshare.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.merpyzf.common.utils.DisplayUtils;
import com.merpyzf.fileserver.FileServer;
import com.merpyzf.common.utils.NetworkUtil;
import com.merpyzf.qrcodescan.google.encoding.EncodingHandler;
import com.merpyzf.common.utils.ApManager;
import com.merpyzf.common.helper.WifiHelper;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.factory.FileInfoFactory;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.receiver.APChangedReceiver;
import com.merpyzf.common.utils.PersonalSettingUtils;
import com.merpyzf.common.utils.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @author wangke
 */
public class WebShareActivity extends BaseActivity {
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.tv_first_step)
    TextView mTvFirstStep;
    @BindView(R.id.tv_second_step)
    TextView mTvSecondStep;
    @BindView(R.id.tv_net_info)
    TextView mTvNetInfo;
    @BindView(R.id.iv_qrcode_share_page)
    ImageView mIvQrCodeSharePage;
    @BindView(R.id.progress_waiting)
    ProgressBar mProgressBar;
    @BindView(R.id.ll_net_info)
    LinearLayout mLlAboveONetInfo;
    @BindView(R.id.btn_change_ap)
    Button mBtnChangeAp;


    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;
    private APChangedReceiver mApChangedReceiver;
    private FileServer mFileServer;
    private WifiHelper mWifiMgr;
    private static String TAG = WebShareActivity.class.getSimpleName();

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_web_share;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("web传");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initData() {
        super.initData();
        mWifiMgr = WifiHelper.getInstance(mContext);
        // 传输的方式
        int transferMode = PersonalSettingUtils.getTransferMode(mContext);
        if (transferMode == PersonalSettingUtils.TRANSFER_MODE_AP) {
            mBtnChangeAp.setVisibility(View.INVISIBLE);
            requestPermissionAndInitAp();
        } else if (transferMode == PersonalSettingUtils.TRANSFER_MODE_LAN) {
            // 如果当前已连接到局域网就使用局域网，如果没有局域网就开启热点
            if (NetworkUtil.isWifi(mContext)) {
                mBtnChangeAp.setVisibility(View.VISIBLE);
                mLlAboveONetInfo.setVisibility(View.INVISIBLE);
                String localIp = NetworkUtil.getLocalIp(mContext);
                String ssid = mWifiMgr.getCurrConnWifiSsid();
                mTvFirstStep.setText("第一步: 邀请好友连接到" + ssid + "网络");
                //开启网页文件共享服务
                FileServer.getInstance()
                        .startupFileShareServer(mContext, localIp, FileInfoFactory.toFileServerType(App.getTransferFileList()));
                String sharePageLink = "http://" + localIp + ":" + com.merpyzf.fileserver.common.Const.DEFAULT_PORT + "/share";
                mTvSecondStep.setText("第二步: 在地址栏输入" + sharePageLink + "\n或通过手机等智能设备直接扫码访问:");
                mIvQrCodeSharePage.setImageBitmap(getQrCode(sharePageLink));
            } else {
                Toast.makeText(mContext, "建立热点进行传输", Toast.LENGTH_SHORT).show();
                requestPermissionAndInitAp();
            }
        }

    }

    @Override
    protected void doCreateEvent() {
        mApChangedReceiver = new APChangedReceiver() {
            @SuppressLint("CheckResult")
            @Override
            public void onApEnableAction() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    io.reactivex.Observable.timer(1000, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(aLong -> {
                                String name = Thread.currentThread().getName();
                                FileServer.getInstance()
                                        .startupFileShareServer(mContext, "192.168.43.1", FileInfoFactory.toFileServerType(App.getTransferFileList()));
                                String sharePageLink = "http://192.168.43.1:" + com.merpyzf.fileserver.common.Const.DEFAULT_PORT + "/share";
                                String apSsid = ApManager.getApSSID(mContext);
                                mTvFirstStep.setText("第一步: 邀请好友连接到\"" + apSsid + "\"网络");
                                mTvSecondStep.setText("第二步: 输入" + sharePageLink + "\n或通过手机等智能设备扫码访问:");
                                mProgressBar.setVisibility(View.INVISIBLE);
                                mIvQrCodeSharePage.setVisibility(View.VISIBLE);
                                mIvQrCodeSharePage.setImageBitmap(getQrCode(sharePageLink));
                            });
                }

            }

            @Override
            public void onApDisAbleAction() {
                FileServer.getInstance().stopRunning();
            }
        };
        IntentFilter intentFilter = new IntentFilter(APChangedReceiver.ACTION_WIFI_AP_STATE_CHANGED);
        mContext.registerReceiver(mApChangedReceiver, intentFilter);
        mBtnChangeAp.setOnClickListener(v -> requestPermissionAndInitAp());
    }

    /**
     * 申请并初始化AP
     */
    private void requestPermissionAndInitAp() {
        if (hasWriteSystemPermission()) {
            initAp();
        } else {
            requestWriteSettingsPerm();
        }
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


    /**
     * 初始化热点
     */
    @SuppressLint("CheckResult")
    private void initAp() {
        mBtnChangeAp.setVisibility(View.INVISIBLE);
        // 如果热点处于开启状态就将其关闭
        if (ApManager.isApOn(mContext)) {
            ApManager.turnOffAp(mContext);
        }
        boolean hasPermission = hasPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasPermission) {
            startHotspot();
            ToastUtils.showShort(mContext, "已经拥有位置权限");
        } else {
            new RxPermissions(mContext)
                    .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(permission -> {
                        if (permission.granted) {
                            startHotspot();
                        }
                    });
        }
    }

    /**
     * 开启热点，兼容Android8.0及以上的设备
     */
    private void startHotspot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mProgressBar.setVisibility(View.VISIBLE);
            mIvQrCodeSharePage.setVisibility(View.INVISIBLE);
            ApManager.configApStateOnAndroidO(mContext, new ApManager.HotspotStateCallback() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mIvQrCodeSharePage.setVisibility(View.VISIBLE);
                    String ssid = reservation.getWifiConfiguration().SSID;
                    String preSharedKey = reservation.getWifiConfiguration().preSharedKey;
                    mLlAboveONetInfo.setVisibility(View.VISIBLE);
                    mFileServer = FileServer.getInstance();
                    mFileServer.startupFileShareServer(mContext, "192.168.43.1", FileInfoFactory.toFileServerType(App.getTransferFileList()));
                    App.setReservation(reservation);
                    String sharePageLink = "http://192.168.43.1:" + com.merpyzf.fileserver.common.Const.DEFAULT_PORT + "/share";
                    Log.i("w3k", "执行了");
                    mTvFirstStep.setText("第一步：邀请好友连接到\"" + ssid + "\"网络");
                    mTvNetInfo.setText("由于Android8.0及以上系统的限制，\n请输入热点密码以连接: \n\n" + preSharedKey);
                    mTvSecondStep.setText("第二步: 输入" + sharePageLink + "\n或通过手机等智能设备扫码访问:");
                    mIvQrCodeSharePage.setVisibility(View.VISIBLE);
                    mIvQrCodeSharePage.setImageBitmap(getQrCode(sharePageLink));
                }

                @Override
                public void onStopped() {
                    Log.i(TAG, "stopped...");
                }

                @Override
                public void onFailed(int reason) {
                    Log.i(TAG, "onFailed...");
                }
            });
        } else {
            String nickName = PersonalSettingUtils.getNickname(mContext);
            int avatar = PersonalSettingUtils.getAvatar(mContext);
            ApManager.configApState(mContext, nickName, avatar);
            mLlAboveONetInfo.setVisibility(View.INVISIBLE);
            mIvQrCodeSharePage.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }


    public static boolean hasPermission(Context context, String permission) {
        int perm = context.checkCallingOrSelfPermission(permission);
        return perm == PackageManager.PERMISSION_GRANTED;
    }


    private void requestWriteSettingsPerm() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (hasWriteSystemPermission()) {
                initAp();
                ToastUtils.showShort(mContext, "授予了修改系统设置的权限");
            } else {
                ToastUtils.showShort(mContext, "修改系统设置的权限未被授予");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public Bitmap getQrCode(String message) {
        Bitmap qrCode = EncodingHandler.createQRCode(message, DisplayUtils.dip2px(mContext, 200));
        return qrCode;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileServer.getInstance().stopRunning();
        if (mApChangedReceiver != null) {
            mContext.unregisterReceiver(mApChangedReceiver);
            mApChangedReceiver = null;
        }
        ApManager.turnOffAp(mContext);
    }
}
