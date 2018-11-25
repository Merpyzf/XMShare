package com.merpyzf.transfermanager.send;

import com.merpyzf.transfermanager.entity.BaseFileInfo;

/**
 * Created by wangke on 2017/12/22.
 */

public interface SendTask {

    /**
     * 初始化
     */
    void connect() throws Exception;

    /**
     * 预先发送待传输的文件列表的信息
     */
    void sendTransferList() throws Exception;

    /**
     * 发送文件的头信息
     */
    void sendHeader(BaseFileInfo fileInfo) throws Exception;

    /**
     *  发送待传输的文件列表
     */
    void sendFileList();

    /**
     * 发送文件
     * @param fileInfo
     */
    void sendFileBody(BaseFileInfo fileInfo);

    /**
     * 通过Handler将消息发送到主线程分发
     * @param fileInfo
     * @param transferStatus
     */
    public void sendMessage(BaseFileInfo fileInfo, int transferStatus);

    /**
     * 通过Handler将异常消息发送到到主线程分发
     * @param e
     */
    public void sendError(Exception e);

    /**
     * 释放资源
     */
    void release();


}
