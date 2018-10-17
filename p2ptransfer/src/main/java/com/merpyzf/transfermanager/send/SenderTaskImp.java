package com.merpyzf.transfermanager.send;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.merpyzf.transfermanager.P2pTransferHandler;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Created by wangke on 2017/12/12.
 * <p>
 * 处理发送任务
 */

public class SenderTaskImp implements SendTask, Runnable {

    private Context mContext;
    private List<FileInfo> mFileList;
    private Socket mSocket;
    private OutputStream mOutputStream;
    private P2pTransferHandler mP2pTransferHandler;
    private String mtargetAddress;
    private static final String TAG = SenderTaskImp.class.getSimpleName();

    /**
     * 文件发送时，先发送待传输的文件列表和缩略图,接着传送文件
     */
    public SenderTaskImp(Context context, String targetAddress, List<FileInfo> sendFileList,
                         P2pTransferHandler p2pTransferHandler) {
        this.mFileList = sendFileList;
        this.mP2pTransferHandler = p2pTransferHandler;
        this.mContext = context;
        this.mtargetAddress = targetAddress;
    }


    @Override
    public void init() throws Exception {
        mSocket = new Socket();
        mSocket.connect(new InetSocketAddress(mtargetAddress, Const.SOCKET_PORT), 3000);
        mOutputStream = mSocket.getOutputStream();
    }

    /**
     * 发送等待传输的文件清单
     */
    @Override
    public void sendTransferList() throws Exception {
        for (FileInfo fileInfo : mFileList) {
            sendHeader(fileInfo);
        }
    }

    /**
     * 发送文件头信息包含缩略图
     * @param fileInfo
     * @throws Exception
     */
    @Override
    public void sendHeader(FileInfo fileInfo) throws Exception {
        String header = fileInfo.getHeader(mContext);
        Log.i("WW2K", "header--> " + header);
        mOutputStream.write(header.getBytes());
        if (fileInfo.getType() != FileInfo.FILE_TYPE_IMAGE) {
            mOutputStream.write(fileInfo.getFileThumbArray());
        }
        mOutputStream.flush();
    }
    @Override
    public void sendFileList() {
        for (FileInfo fileInfo : mFileList) {
            sendFileBody(fileInfo);
        }
    }
    /**
     * 发送文件的内容
     *
     * @param fileInfo
     */
    @Override
    public void sendFileBody(FileInfo fileInfo) {
        File file = new File(fileInfo.getPath());
        long fileLength = file.length();
        // 记录每秒读取的字节长度
        int perSecondReadLength = 0;
        byte[] buffer = new byte[Const.BUFFER_LENGTH];
        int readLength = 0;
        // 每一次读取的字节长度
        int currentLength;
        long start = System.currentTimeMillis();
        long startTime = System.currentTimeMillis();
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            // 设置传输文件对象的状态为: 传输中
            fileInfo.setFileTransferStatus(Const.TransferStatus.TRANSFING);
            while ((currentLength = bis.read(buffer)) != -1) {
                mOutputStream.write(buffer, 0, currentLength);
                readLength += currentLength;
                long endTime = System.currentTimeMillis();
                perSecondReadLength += currentLength;
                // 统计每秒的传输速度
                if (endTime - startTime >= 1000) {
                    // TODO: 2018/10/15 考虑传输速度的信息放置在FileInfo对象中是否合适
                    String[] perSecondSpeed = FileUtils.getFileSizeArrayStr(perSecondReadLength);
                    fileInfo.setTransferSpeed(perSecondSpeed);
                    perSecondReadLength = 0;
                    startTime = endTime;
                }
                long end = System.currentTimeMillis();
                // 控制界面传输进度的刷新间隔为50毫秒
                if (end - start > Const.PROGRESS_REFRESH_INTERVAL) {
                    fileInfo.setProgress((readLength / (fileLength * 1.0f)));
                    // 发送文件正在传输的消息，通知界面更新传输进度
                    sendMessage(fileInfo, Const.TransferStatus.TRANSFING);
                    start = end;
                }
            }
            mOutputStream.flush();
            // 发送文件传输成功的消息
            sendMessage(fileInfo, Const.TransferStatus.TRANSFER_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(e);
            release();
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
                sendError(e);
            }
        }


    }

    @Override
    public void run() {
        try {
            init();
            sendTransferList();
            sendFileList();
        } catch (Exception e) {
            e.printStackTrace();
            sendError(e);
        } finally {
            // 传输完毕后的资源释放
            release();
        }

    }
    @Override
    public void sendMessage(FileInfo fileInfo, int transferStatus) {
        fileInfo.setFileTransferStatus(transferStatus);
        Message message = mP2pTransferHandler.obtainMessage();
        message.what = transferStatus;
        message.obj = fileInfo;
        mP2pTransferHandler.sendMessage(message);
    }

    /**
     * 发送错误消息给主线程
     *
     * @param e
     */
    @Override
    public void sendError(Exception e) {
        Message message = mP2pTransferHandler.obtainMessage();
        message.what = Const.TransferStatus.TRANSFER_FAILED;
        message.obj = e;
        mP2pTransferHandler.sendMessage(message);
    }

    /**
     * 释放资源
     */
    @Override
    public void release() {
        try {
            if (mOutputStream != null) {
                mOutputStream.close();
            }
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
