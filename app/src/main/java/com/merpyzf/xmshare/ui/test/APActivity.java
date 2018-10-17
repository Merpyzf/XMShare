package com.merpyzf.xmshare.ui.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.interfaces.TransferObserver;
import com.merpyzf.transfermanager.receive.ReceiverManager;
import com.merpyzf.transfermanager.util.ApManager;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.receiver.APChangedReceiver;
import com.merpyzf.xmshare.util.SingleThreadPool;

/**
 * 建立热点
 * 文件接收端
 */
public class APActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;
    private APChangedReceiver mApChangedReceiver;
    private Activity mContext;

    public static void start(Context context) {

        context.startActivity(new Intent(context, APActivity.class));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ap);
        mContext = this;
        requestPermissionAndInitAp();


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

            // 拥有权限直接建立热点
            initAP();

        } else {
            // 没有权限则去进行申请
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // 6.0以上设备的权限申请方式

                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);

            } else {

                // 6.0一下的设备进行权限申请的方式
                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.WRITE_SETTINGS}, REQUEST_CODE_WRITE_SETTINGS);

            }

        }


    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("w2k", "--> onRequestPermissionsResult");
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS && Settings.System.canWrite(this)) {
            Log.i("w2k", "权限申请成功");
            initAP();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.i("w2k", "--> onRequestPermissionsResult");

        if (requestCode == REQUEST_CODE_WRITE_SETTINGS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("w2k", "权限通过");
            initAP();
        } else {
            Toast.makeText(mContext, "权限被拒绝，无法创建热点", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 初始化热点
     */
    private void initAP() {
        // 如果热点处于开启状态就将其关闭
        if (ApManager.isApOn(this)) {
            ApManager.turnOffAp(this);
        }

        mApChangedReceiver = new APChangedReceiver() {
            @Override
            public void onApEnableAction() {

                Toast.makeText(mContext, "Ap初始化成功", Toast.LENGTH_SHORT).show();
                ReceiverManager receiverManager = ReceiverManager.getInstance(mContext);

                receiverManager.register(new TransferObserver() {
                    @Override
                    public void onTransferProgress(FileInfo fileInfo) {
                        Log.i("w2k", "传输进度: " + (int) (fileInfo.getProgress() * 100));
                        String[] transferSpeed = fileInfo.getTransferSpeed();

                        if (transferSpeed != null) {
                            Log.i("w2k", "传输速度: " + transferSpeed[0] + transferSpeed[1] + " /s");
                        }
                    }

                    @Override
                    public void onTransferStatus(FileInfo fileInfo) {

                        Log.i("w2k", "传输状态->  " + fileInfo.getName());

                    }

                    @Override
                    public void onTransferError(String error) {

                    }
                });

                SingleThreadPool.getSingleton().execute(receiverManager);

            }

            @Override
            public void onApDisAbleAction() {

                // 热点被关闭的回调方法

            }
        };


        IntentFilter intentFilter = new IntentFilter(APChangedReceiver.ACTION_WIFI_AP_STATE_CHANGED);
        registerReceiver(mApChangedReceiver, intentFilter);
        // 设置一个昵称
        String nickName = "macbook";
        int avatar = 1;
        // 开启一个热点
        ApManager.configApState(this, nickName, avatar);
    }

    @Override
    protected void onDestroy() {
        // 解除广播的注册
        if (mApChangedReceiver != null) {
            unregisterReceiver(mApChangedReceiver);
        }
        super.onDestroy();

    }
}
