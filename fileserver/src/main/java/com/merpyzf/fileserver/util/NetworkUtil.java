package com.merpyzf.fileserver.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by wangke on 2017/12/1.
 * 网络相关的Util
 */

public class NetworkUtil {

    private static final String TAG = "NetworkUtil";

    /**
     * 获取本机ip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 检查wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            return null;
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ip = intToIp(wifiInfo.getIpAddress());
        return ip;
    }

    private static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "."
                + (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
    }
    /**
     * 获取广播地址
     *
     * @param context
     * @return
     * @throws UnknownHostException
     */
    public static InetAddress getBroadcastAddress(Context context)
            throws UnknownHostException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if (dhcp == null) {
            return InetAddress.getByName("255.255.255.255");
        }
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }
        return InetAddress.getByAddress(quads);
    }

    /**
     * 判断传入的ip地址是否为本机ip
     *
     * @param ip
     * @return
     */
    public static boolean isLocal(String ip) {
        String[] localIP = getLocalAllIP();
        for (String s : localIP) {
            if (ip.equals(s)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 获取本机硬件设备绑定的所有IP地址
     *
     * @return
     */
    private static String[] getLocalAllIP() {
        ArrayList<String> iPs = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && isIpv4Address(ip.getHostAddress())) {
                        iPs.add(ip.getHostAddress());
                    }
                }

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return (String[]) iPs.toArray(new String[]{});
    }
    /**
     * 判断ip地址的格式是否为
     *
     * @param ipAddress
     * @return
     */
    public static boolean isIpv4Address(String ipAddress) {
        if (ipAddress == null || ipAddress.length() == 0) {
            //字符串为空或者空串
            return false;
        }
        //因为java doc里已经说明, split的参数是reg, 即正则表达式, 如果用"|"分割, 则需使用"\\|"
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) {
            //分割开的数组根本就不是4个数字
            return false;
        }
        for (String part : parts) {
            try {
                int n = Integer.parseInt(part);
                if (n < 0 || n > 255) {
                    //数字不在正确范围内
                    return false;
                }
            } catch (NumberFormatException e) {
                //转换数字不正确
                return false;
            }
        }
        return true;
    }


    /**
     * 获取MAC地址
     *
     * @param mContext
     * @return
     */
    public static String getMacAddress(Context mContext) {
        String macStr = "";
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getMacAddress() != null) {
            macStr = wifiInfo.getMacAddress();// MAC地址
        } else {
            macStr = "null";
        }

        return macStr;
    }

    /**
     * 判断指定的ipAddress是否可以ping
     *
     * @param ipAddress
     * @return
     */
    public static boolean pingIpAddress(String ipAddress) {
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 100 " + ipAddress);
            int status = process.waitFor();
            if (status == 0) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取ssid
     *
     * @param nickName 用户名
     * @param avatar   头像id
     * @return
     */
    public static String getSSID(String nickName, int avatar) {
        String ssid = "XM" + (FormatUtils.string2Unicode(avatar + "") + "-" +
                FormatUtils.string2Unicode(nickName)).replaceAll("\\\\u", "T");
        return ssid;
    }

    /**
     * 从AP名中获取用户名和头像
     *
     * @return
     */
    public static String[] getApNickAndAvatar(String ssid) {

        String[] apInfo = new String[2];
        String[] split = ssid.split("-");
        // 头像
        String avatar = FormatUtils.unicode2String((split[0].substring(2)));
        // 用户名
        String nickName = FormatUtils.unicode2String(split[1]);
        apInfo[0] = avatar;
        apInfo[1] = nickName;
        return apInfo;

    }

    /**
     * 判断当前是否是wifi环境
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
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


}
