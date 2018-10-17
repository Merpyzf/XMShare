package com.merpyzf.transfermanager.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wangke on 2018/1/25.
 */

public class ApManager {

    /**
     * 获取无线热点当前的状态
     *
     * @return
     */
    public static boolean isApOn(Context context) {

        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        } catch (Throwable ignored) {
        }
        return false;

    }

    /**
     * 关闭无线热点
     *
     * @param context
     */
    public static void turnOffAp(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, null, false);
        } catch (Throwable ignored) {

        }
    }

    /**
     * 开启热点，热点的ssid和密码由系统随机指配
     *
     * @param context 上下文
     * @return 开启状态
     */
    public static boolean configApState(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            // if WiFi is on, turn it off
            if (wifimanager.isWifiEnabled()) {
                wifimanager.setWifiEnabled(false);
            }


            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isApOn(context));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 开启热点，热点的ssid可自行指配
     *
     * @param context  上下文
     * @param nickName 昵称
     * @param avatar   头像
     * @return 开启状态
     */
    public static boolean configApState(Context context, String nickName, int avatar) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager mCm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiConfiguration wificonfiguration = null;

        try {
            wificonfiguration = new WifiConfiguration();
            wificonfiguration.SSID = NetworkUtil.getSSID(nickName, avatar);

            // if WiFi is on, turn it off
            if (wifimanager.isWifiEnabled()) {
                wifimanager.setWifiEnabled(false);
                // if ap is on and then disable ap
                if (isApOn(context)) {
                    turnOffAp(context);
                }
            }


            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isApOn(context));

            // Android6.0以上的设备需要进行特殊处理
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.i("wk", "当前运行的设备为Android6.0以上的设备");


            }


            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Android8.0以上的设备调用此方法开启热点，受系统限制开启的热点信息由系统指配
     *
     * @param context              上下文
     * @param hotspotStateCallback 热点状态回调用
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void configApStateOnAndroidO(Context context, final HotspotStateCallback hotspotStateCallback) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
            @Override
            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                super.onStarted(reservation);

                if (hotspotStateCallback != null) {
                    hotspotStateCallback.onStarted(reservation);
                }
            }

            @Override
            public void onStopped() {
                super.onStopped();
                if (hotspotStateCallback != null) {
                    hotspotStateCallback.onStopped();
                }
            }

            @Override
            public void onFailed(int reason) {
                super.onFailed(reason);
                if (hotspotStateCallback != null) {
                    hotspotStateCallback.onFailed(reason);
                }
            }
        }, new Handler());


    }


    /**
     * 获取热点的ssid
     */
    public static String getApSSID(Context mContext) {
        String ssid = null;
        try {
            WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            //拿到getWifiApConfiguration()方法
            Method method = manager.getClass().getDeclaredMethod("getWifiApConfiguration");
            //调用getWifiApConfiguration()方法，获取到 热点的WifiConfiguration
            WifiConfiguration configuration = (WifiConfiguration) method.invoke(manager);
            ssid = configuration.SSID;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return ssid;
    }


    public interface HotspotStateCallback {
        /**
         * 热点开启
         *
         * @param reservation
         */
        void onStarted(WifiManager.LocalOnlyHotspotReservation reservation);

        /**
         * 热点被关闭
         */
        void onStopped();

        /**
         * 开启失败
         *
         * @param reason
         */
        void onFailed(int reason);
    }


}
