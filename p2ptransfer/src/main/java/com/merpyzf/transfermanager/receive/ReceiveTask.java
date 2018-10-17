package com.merpyzf.transfermanager.receive;

import com.merpyzf.transfermanager.entity.FileInfo;

/**
 * Created by wangke on 2017/12/22.
 */

public interface ReceiveTask {

    /**
     * 资源的初始化
     */
    void init();

    /**
     * 接收待接收文件列表
     */
    void receiveTransferList() throws Exception;

    void receiveFileList() throws Exception;

    /**
     * 读取文件
     */
    void receiveBody(FileInfo fileInfo) throws Exception;

    /**
     * 通过Handler将消息发送到主线程分发
     * @param obj
     * @param transferStatus
     */
    public void sendMessage(Object obj, int transferStatus);

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
