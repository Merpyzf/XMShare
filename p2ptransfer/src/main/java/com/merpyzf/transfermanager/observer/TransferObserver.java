package com.merpyzf.transfermanager.observer;

import com.merpyzf.transfermanager.entity.BaseFileInfo;

/**
 * Created by wangke on 2018/1/22.
 * 文件传输过程中的观察者的回调事件
 */

public interface TransferObserver {

    /**
     * 文件接收进度的回调
     */
    public void onTransferProgress(BaseFileInfo fileInfo);

    /**
     * 文件正常接收状态的回调
     */
    public void onTransferStatus(BaseFileInfo fileInfo);

    /**
     * 传输出错
     *
     * @param error
     */
    public void onTransferError(String error);

}