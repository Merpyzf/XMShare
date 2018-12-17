package com.merpyzf.xmshare.ui.interfaces;

import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.Peer;

import java.util.List;

/**
 * description:
 * author: wangke
 * date: 2018/8/16.
 * version:
 */
public abstract class OnPairActionListener {

    /**
     * 发送建立连接的请求
     */
    public void onSendConnRequest() {
    }

    public void onOffline(Peer peer) {


    }

    /**
     * 配对成功
     *
     * @param peer
     */
    public void onPairSuccess(Peer peer) {

    }

    /**
     * 配对失败
     *
     * @param peer
     */
    public void onPeerPairFailed(Peer peer) {

    }

    /**
     * 向接收端发送文件
     *
     * @param peer        对端的主机信息
     * @param fileInfoLis 待发送的文件列表
     */
    public void onStartTransfer(Peer peer, List<BaseFileInfo> fileInfoLis) {

    }


}