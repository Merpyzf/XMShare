package com.merpyzf.xmshare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

/**
 * 监听WLAN状态发生变化的广播
 */
public abstract class WifiChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

            switch (state) {

                case WifiManager.WIFI_STATE_DISABLED:
                    onWifiDisableAction();

                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    onWifiEnableAction();
                    break;

                default:
                    break;

            }
        }

    }

    // wifi 开启
    public abstract void onWifiEnableAction();

    // wifi 关闭
    public abstract void onWifiDisableAction();
}
