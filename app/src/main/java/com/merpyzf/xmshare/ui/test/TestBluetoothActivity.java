package com.merpyzf.xmshare.ui.test;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.merpyzf.xmshare.R;

import java.io.File;

/**
 * 蓝牙相关知识学习
 * <p>
 * Android支持的蓝牙协议栈: Bluz、 BlueDroid、BLE
 */
public class TestBluetoothActivity extends AppCompatActivity {

    private static final String TAG = TestBluetoothActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bluetooth);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {

            Log.i(TAG, "当前蓝牙设备不可用");
        } else {

            // 如果蓝牙不可用
            if (!bluetoothAdapter.enable()) {

                // 开启蓝牙
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                // 通过监听广播来确定当前蓝牙的开启状态


            }


            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
//      intent.setType("application/octet-stream");
            intent.setType("*/*"); //
            intent.setClassName("com.android.bluetooth"
                    , "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile( new File("/sdcard/春望海报打印.jpg")));
            startActivity(intent);


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "requestCode-> " + requestCode + " resultCode->" + resultCode);

        super.onActivityResult(requestCode, resultCode, data);

    }
}
