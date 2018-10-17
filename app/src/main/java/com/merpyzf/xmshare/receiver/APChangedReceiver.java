package com.merpyzf.xmshare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 监听AP热点状态的广播接受者
 * <p>
 * Created by wangke on 2017/12/25.
 */
public abstract class APChangedReceiver extends BroadcastReceiver {
    public static final String TAG = APChangedReceiver.class.getSimpleName();
    public static final String ACTION_WIFI_AP_STATE_CHANGED = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    private boolean flag = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        // wifi的状态发生变化
        if (action.equals(ACTION_WIFI_AP_STATE_CHANGED)) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.i(TAG, "Wifi Ap state--->>>" + state);
            if (WifiManager.WIFI_STATE_ENABLED == state % 10) {
                Log.i(TAG, "AP 可用状态...");
                onApEnableAction();
            } else if (WifiManager.WIFI_STATE_ENABLING == state % 10) {

                // wifi 正在开启
                Log.i(TAG, "AP 正在开启状态...");
            } else if (WifiManager.WIFI_STATE_DISABLED == state % 10) {
                if (flag) {
                    onApDisAbleAction();
                    flag = false;
                }
                // wifi 已不可用

                Log.i(TAG, "AP 已不可用状态...");

            } else if (WifiManager.WIFI_STATE_DISABLING == state % 10) {
                flag = true;
                // wifi 关闭中
                Log.i(TAG, "AP 正在关闭状态...");
            }
        }
    }

    public abstract void onApEnableAction();

    public abstract void onApDisAbleAction();
}
