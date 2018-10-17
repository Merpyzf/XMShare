package com.merpyzf.transfermanager.interfaces;

import com.merpyzf.transfermanager.entity.Peer;

/**
 * Created by wangke on 2017/12/13.
 * 搜寻局域网内设备的事件回调
 */
public interface PeerCommCallback {

    /**
     * 设备上线
     *
     * @param peer
     */
    void onDeviceOnLine(Peer peer);

    /**
     * 设备离线
     *
     * @param peer
     */
    void onDeviceOffLine(Peer peer);

    /**
     * 请求建立连接
     *
     * @param peer
     */
    void onRequestConnect(Peer peer);

    /**
     * 对端对请求建立连接请求的回应
     *
     * @param peer
     */
    void onAnswerRequestConnect(Peer peer);


}
