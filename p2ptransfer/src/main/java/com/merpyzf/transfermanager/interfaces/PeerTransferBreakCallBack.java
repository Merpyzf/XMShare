package com.merpyzf.transfermanager.interfaces;

import com.merpyzf.transfermanager.entity.Peer;

/**
 * Created by wangke on 2018/1/28.
 * 接收到提前中断传输的回调
 */

public interface PeerTransferBreakCallBack {

    /**
     * 中断传输
     */
    void onTransferBreak(Peer peer);
}
