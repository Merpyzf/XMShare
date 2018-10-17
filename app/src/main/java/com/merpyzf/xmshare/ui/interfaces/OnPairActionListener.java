package com.merpyzf.xmshare.ui.interfaces;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.Peer;

import java.util.List;

/**
 * description:
 * author: wangke
 * date: 2018/8/16.
 * version:
 */
public interface OnPairActionListener {

    /**
     * 发送建立连接的请求
     */
    void onSendConnRequest();

    /**
     * 配对成功
     *
     * @param peer
     */
    void onPairSuccess(Peer peer);

    /**
     * 配对失败
     *
     * @param peer
     */
    void onPeerPairFailed(Peer peer);

    /**
     * 向接收端发送文件
     *
     * @param peer        对端的主机信息
     * @param fileInfoLis 待发送的文件列表
     */
    void onStartTransfer(Peer peer, List<FileInfo> fileInfoLis);


}