package com.merpyzf.transfermanager.entity;

import com.merpyzf.transfermanager.common.Const;

/**
 * Created by wangke on 2017/12/13.
 * 描述设备的对象
 */

public class Peer {

    // 昵称
    private String nickName;

    // 头像下标
    private int avatarPosition;

    // 主机地址
    private String hostAddress;

    private String ssid;

    // 标记是否是无线热点
    private boolean isHotsPot = false;
    private String preSharedKey;


    public Peer() {

    }

    public Peer(String nickName, String hostAddress) {
        this.nickName = nickName;
        this.hostAddress = hostAddress;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }


    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public boolean isHotsPot() {
        return isHotsPot;
    }

    public void setHotsPot(boolean hotsPot) {
        isHotsPot = hotsPot;
    }

    public void setPreSharedKey(String preSharedKey) {
        this.preSharedKey = preSharedKey;
    }

    public String getPreSharedKey() {
        return preSharedKey;
    }

    public int getAvatarPosition() {
        return avatarPosition;
    }

    public void setAvatarPosition(int avatarPosition) {
        this.avatarPosition = avatarPosition;
    }


    public boolean isAndroidODevice(String ssid) {

        if (ssid.startsWith(Const.HOTSPOT_PREFIX_IDENT_O)) {
            return true;
        }
        return false;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Peer peer = (Peer) o;
        return hostAddress.equals(peer.hostAddress);
    }

    @Override
    public int hashCode() {
        return hostAddress.hashCode();
    }


}
