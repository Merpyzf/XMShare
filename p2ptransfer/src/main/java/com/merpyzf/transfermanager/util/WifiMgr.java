package com.merpyzf.transfermanager.util;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by wangke on 2018/1/25.
 */

public class WifiMgr {

    private static WifiMgr mWifiMgr;
    private WifiManager mWifiManager;
    private List<ScanResult> mScanResults;
    private List<WifiConfiguration> mConfiguredNetworks;

    /**
     * 创建WifiConfiguration的类型
     */
    public static final int WIFICIPHER_NONE = 0;
    public static final int WIFICIPHER_NOPASS = 1;
    public static final int WIFICIPHER_WEP = 2;
    public static final int WIFICIPHER_WPA = 3;
    private WifiInfo mWifiInfo;


    private WifiMgr(Context context) {
        this.mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }


    public static WifiMgr getInstance(Context context) {

        if (mWifiMgr == null) {
            synchronized (Object.class) {
                if (mWifiMgr == null) {
                    mWifiMgr = new WifiMgr(context);
                }
            }

        }
        return mWifiMgr;
    }

    /**
     * 打开wifi
     */
    public void openWifi() {

        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }

    }

    /**
     * 关闭Wifi
     */
    public void closeWifi() {

        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }

    }

    /**
     * 判断wifi是否开启的状态
     *
     * @return
     */
    public boolean isWifiEnable() {
        return mWifiManager == null ? false : mWifiManager.isWifiEnabled();
    }

    /**
     * 开始扫描
     */
    public List<ScanResult> startScan() {

        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.startScan();
            mScanResults = mWifiManager.getScanResults();
            mConfiguredNetworks = mWifiManager.getConfiguredNetworks();
            return mScanResults;
        }

        return null;

    }


    public List<ScanResult> getScanResults() {
        return mScanResults;
    }

    public List<WifiConfiguration> getConfiguredNetworks() {
        return mConfiguredNetworks;
    }


    /**
     * 连接到指定的Wifi网络
     */
    public boolean connectNewWifi(WifiConfiguration wifiCfg) {
        // 断开当前的连接
        disconnectCurrentNetwork();
        int netWorkId = mWifiManager.addNetwork(wifiCfg);
        return mWifiManager.enableNetwork(netWorkId, true);
    }


    /**
     * 关闭当前的Wifi网络
     *
     * @return
     */
    public boolean disconnectCurrentNetwork() {
        if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
            int netId = mWifiManager.getConnectionInfo().getNetworkId();
            mWifiManager.disableNetwork(netId);
            return mWifiManager.disconnect();
        }
        return false;
    }


    /**
     * 创建WifiConfiguration
     *
     * @param ssid
     * @param password
     * @param type
     * @return
     */
    public static WifiConfiguration createWifiCfg(String ssid, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        config.SSID = "\"" + ssid + "\"";

        if (type == WIFICIPHER_NOPASS) {
//            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;

//            无密码连接WIFI时，连接不上wifi，需要注释两行代码
//            config.wepKeys[0] = "";
//            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WEP) {
//            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            // 注释一下代码，不然连接不上有密码的热点
//            config.hiddenSSID = true;
//            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    /**
     * 获取当前连接的WIfi信息
     *
     * @return
     */
    public WifiInfo getCurrConnWifiInfo() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo;
    }


    /**
     * 获取当前Wifi所分配的Ip地址
     *
     * @return
     */
//  when connect the hotspot, is still returning "0.0.0.0".
    public String getCurrentIpAddress() {
        String ipAddress = "";
        int address = mWifiManager.getDhcpInfo().ipAddress;
        ipAddress = ((address & 0xFF)
                + "." + ((address >> 8) & 0xFF)
                + "." + ((address >> 16) & 0xFF)
                + "." + ((address >> 24) & 0xFF));
        return ipAddress;
    }


    /**
     * 设备连接Wifi之后， 设备获取Wifi热点的IP地址
     *
     * @return
     */
    public String getIpAddressFromHotspot() {
        // WifiAP ip address is hardcoded in Android.
        String ipAddress = "192.168.43.1";
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        int address = dhcpInfo.gateway;
        ipAddress = ((address & 0xFF)
                + "." + ((address >> 8) & 0xFF)
                + "." + ((address >> 16) & 0xFF)
                + "." + ((address >> 24) & 0xFF));
        return ipAddress;
    }


    /**
     * 开启热点之后，获取自身热点的IP地址
     *
     * @return
     */
    public String getHotspotLocalIpAddress() {
        // WifiAP ip address is hardcoded in Android.
        /* IP/netmask: 192.168.43.1/255.255.255.0 */
        String ipAddress = "192.168.43.1";
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        int address = dhcpInfo.serverAddress;
        ipAddress = ((address & 0xFF)
                + "." + ((address >> 8) & 0xFF)
                + "." + ((address >> 16) & 0xFF)
                + "." + ((address >> 24) & 0xFF));
        return ipAddress;
    }


    /**
     * 关闭Wifi
     */
    public void disableWifi() {
        if (mWifiManager != null) {
            mWifiManager.setWifiEnabled(false);
        }
    }


//    static int getSecurity(WifiConfiguration config) {
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
//            return WIFICIPHER_WPA;
//        }
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
//            return WIFICIPHER_WEP;
//        }
//        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
//    }
}
