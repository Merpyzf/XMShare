package com.merpyzf.xmshare.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.merpyzf.fileserver.FileServer;
import com.merpyzf.qrcodescan.google.encoding.EncodingHandler;
import com.merpyzf.transfermanager.util.ApManager;
import com.merpyzf.transfermanager.util.WifiMgr;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.factory.FileInfoFactory;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.receiver.APChangedReceiver;
import com.merpyzf.xmshare.util.DisplayUtils;
import com.merpyzf.xmshare.util.SharedPreUtils;
import com.merpyzf.xmshare.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

public class WebShareActivity extends BaseActivity {
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.tv_info)
    TextView mTvInfo;

    private static String TAG = "ww2k";
    private WifiMgr mWifiMgr;
    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;
    private APChangedReceiver mApChangedReceiver;
    private FileServer mFileServer;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_web_share;
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("web传");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void initData() {
        super.initData();
        // 传输的方式
        int transferMode = SharedPreUtils.getInteger(mContext, Const.SP_USER, Const.KEY_TRANSFER_MODE,
                Const.TRANSFER_MODE_LAN);
        if (transferMode == Const.TRANSFER_MODE_AP) {
            // 热点传输优先-> 无论是否连接wifi都要建立热点
            mFileServer = FileServer.getInstance();
            mFileServer.startupFileShareServer(mContext, "192.168.43.1", FileInfoFactory.toFileServerType(App.getTransferFileList()));
            //requestPermissionAndInitAp();
            //mBtnChangedAp.setVisibility(View.INVISIBLE);
        } else if (transferMode == Const.TRANSFER_MODE_LAN) {
            // 局域网传输优先-> 如果没有连接wifi 就开启热点
            //mWifiMgr = WifiMgr.getInstance(mContext);
            //if (NetworkUtil.isWifi(mContext)) {
            //    Log.i(TAG, "当前wifi处于WIFI环境");
            //    String localIp = NetworkUtil.getLocalIp(mContext);
            //    Log.i(TAG, "localIp: " + localIp);
            //    mTvInfo.setText("访问: " + "http://" + localIp + ":8888");
            //
            //    mFileServer.startupFileShareServer(mContext, "192.168.43.1", FileInfoFactory.toFileServerType(App.getTransferFileList()));
            //    //mBtnChangedAp.setVisibility(View.VISIBLE);
            //} else {
            //    Toast.makeText(mContext, "建立热点进行传输", Toast.LENGTH_SHORT).show();
            //    requestPermissionAndInitAp();
            //    //mBtnChangedAp.setVisibility(View.INVISIBLE);
            //}
        }

    }

    @Override
    protected void initEvents() {

        //1. 如果当前处在局域网的环境
        //    1.1 显示局域网的名称，以及文件共享页面的主页
        //    2.2 提供自行组建局域网的按钮选项

        //2. 如果当前设备已经开启热点
        // 8.0一下的设备无密码直接连接
        // 8.0以上的设备需要密码，提供一个包含用户名和密码的选项供用户连接


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

            Log.i("wk", "拥有权限直接建立热点");

            //closeMobileNetwork();
            // 拥有权限直接建立热点
            initAp();

        } else {

            requestWriteSettings();
        }
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
        // 热点被关闭的回调方法
        mApChangedReceiver = new APChangedReceiver() {
            @Override
            public void onApEnableAction() {
                ToastUtils.showShort(mContext, "热点开启了");
                mTvInfo.setText("http://192.168.43.1:8888");

            }

            @Override
            public void onApDisAbleAction() {
                // 热点被关闭的回调方法
                //mFileServer.stopRunning();
            }
        };

        IntentFilter intentFilter = new IntentFilter(APChangedReceiver.ACTION_WIFI_AP_STATE_CHANGED);
        mContext.registerReceiver(mApChangedReceiver, intentFilter);
        // 开启热点
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 申请位置权限
            new RxPermissions(mContext)
                    .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(permission -> {
                        if (permission.granted) {
                            ApManager.configApStateOnAndroidO(mContext, new ApManager.HotspotStateCallback() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                                    String ssid = reservation.getWifiConfiguration().SSID;
                                    String preSharedKey = reservation.getWifiConfiguration().preSharedKey;
                                    ToastUtils.showShort(mContext, "开启成功: ssid " + ssid + " key:" + preSharedKey);

                                    mTvInfo.setText("请先连接: " + ssid + "密码: " + ssid + "\nhttp://192.168.43.1:8888");
                                    mFileServer = FileServer.getInstance();
                                    mFileServer.startupFileShareServer(mContext, "192.168.43.1", FileInfoFactory.toFileServerType(App.getTransferFileList()));


                                    App.setReservation(reservation);
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("ssid", reservation.getWifiConfiguration().SSID);
                                        jsonObject.put("preSharedKey", reservation.getWifiConfiguration().preSharedKey);
                                        String hotspotInfo = jsonObject.toString();
                                        Bitmap bmpLogo = BitmapFactory.decodeResource(getResources(), SharedPreUtils.getAvatar(mContext));
                                        Bitmap qrCode = EncodingHandler.createQRCode(hotspotInfo, DisplayUtils.dip2px(mContext, 200), DisplayUtils.dip2px(mContext, 200), bmpLogo);
                                        //mIvQrCode.setImageBitmap(qrCode);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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
                        }
                    });
        } else {
            ApManager.configApState(mContext, SharedPreUtils.getNickName(mContext), SharedPreUtils.getAvatar(mContext));
            //mRlShowQrCodeInfo.setVisibility(View.INVISIBLE);
            //radar.setVisibility(View.VISIBLE);
        }
    }


    private void requestWriteSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFileServer.isRunning()) {
            mFileServer.stopRunning();
        }
    }
}
