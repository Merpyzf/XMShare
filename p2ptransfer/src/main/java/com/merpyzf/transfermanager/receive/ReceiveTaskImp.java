package com.merpyzf.transfermanager.receive;

import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.merpyzf.transfermanager.P2pTransferHandler;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.FileInfoFactory;
import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.transfermanager.util.Md5Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangke on 2017/12/22.
 */

public class ReceiveTaskImp implements Runnable, ReceiveTask {

    private Socket mSocketClient;
    private InputStream mInputStream;
    private P2pTransferHandler mP2pTransferHandler;
    private List<FileInfo> mReceiveFileList;
    private Context mContext;
    private static final String TAG = ReceiveTaskImp.class.getSimpleName();
    private boolean isStop = false;

    public ReceiveTaskImp(Context context, Socket socket, P2pTransferHandler receiveHandler) {
        this.mContext = context;
        this.mSocketClient = socket;
        this.mP2pTransferHandler = receiveHandler;
    }

    @Override
    public void init() {
        try {
            mInputStream = mSocketClient.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收文件头信息列表
     * @throws Exception
     */
    @Override
    public synchronized void receiveTransferList() throws Exception {
        mReceiveFileList = new ArrayList<>();
        while (true) {
            FileInfo fileInfo = decodeFileHeader(mInputStream);
            receiveThumbToLocal(fileInfo);
            mReceiveFileList.add(FileInfoFactory.convertFileType(fileInfo));
            if (fileInfo.getIsLast() == Const.IS_LAST) {
                break;
            }
        }
        sendMessage(mReceiveFileList, Const.TransferStatus.TRANSFER_FILE_LIST_SUCCESS);
    }

    @Override
    public synchronized void run() {
        try {
            init();
            receiveTransferList();
            receiveFileList();
        } catch (Exception e) {
            e.printStackTrace();
            sendError(e);
            release();
        } finally {
            release();
        }
    }

    @Override
    public void receiveFileList() throws Exception {
        int receiveIndex = 0;
        while (!isStop) {
            FileInfo fileInfo = mReceiveFileList.get(receiveIndex++);
            receiveBody(fileInfo);
            if (fileInfo.getIsLast() == Const.IS_LAST) {
                break;
            }
        }
    }
    /**
     * 接收文件的缩略图存储到本地
     *
     * @param fileInfo
     */
    private void receiveThumbToLocal(FileInfo fileInfo) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File destFile = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), Md5Utils.getMd5(fileInfo.getName()));
            if (fileInfo.getThumbLength() != 0) {
                FileUtils.writeStream2SdCard(destFile, mInputStream, fileInfo.getThumbLength());
            }
        }

    }

    private FileInfo decodeFileHeader(InputStream inputStream) throws Exception {
        byte[] buffer = new byte[Const.FILE_HEADER_LENGTH];
        int readLength = inputStream.read(buffer, 0, buffer.length);
        if (readLength == 0) {
            throw new Exception("未读取到文件的头信息");
        }
        String strHeader = new String(buffer, Const.S_CHARSET);
        strHeader = trimLastSpaces(strHeader);
        FileInfo fileInfo = new FileInfo();
        fileInfo.decodeHeader(strHeader);
        return fileInfo;
    }

    /**
     * 移除文件头信息末尾为凑齐字长而添加的空格字符
     *
     * @param strHeader
     * @return
     */
    private String trimLastSpaces(String strHeader) {
        return strHeader.substring(0, strHeader.indexOf(Const.S_END));
    }

    /**
     * 进行文件部分字节的读取
     *
     * @param fileInfo
     */
    @Override
    public synchronized void receiveBody(FileInfo fileInfo) {
        // 读取文件的总长度
        int totalLength = fileInfo.getLength();
        int currentLength = 0;
        int readLength = -1;
        int perSecondReadLength = 0;
        byte[] buffer = new byte[Const.BUFFER_LENGTH];
        // 设置文件的传输状态
        fileInfo.setFileTransferStatus(Const.TransferStatus.TRANSFING);
        File saveFile = FileUtils.getSaveFile(fileInfo);
        long start = System.currentTimeMillis();
        long startTime = System.currentTimeMillis();
        long endTime;
        long end;
        float progress;
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(saveFile));
            while (currentLength < totalLength) {
                int leftLength = totalLength - currentLength;
                if (leftLength >= Const.BUFFER_LENGTH) {
                    readLength = mInputStream.read(buffer, 0, Const.BUFFER_LENGTH);
                } else {
                    readLength = mInputStream.read(buffer, 0, leftLength);
                }
                bos.write(buffer, 0, readLength);
                currentLength += readLength;
                perSecondReadLength += readLength;
                end = System.currentTimeMillis();
                endTime = System.currentTimeMillis();
                // 计算每秒传输字节长度
                if (endTime - startTime >= 1000) {
                    String[] transferSpeed = FileUtils.getFileSizeArrayStr(perSecondReadLength);
                    fileInfo.setTransferSpeed(transferSpeed);
                    perSecondReadLength = 0;
                    startTime = endTime;
                }
                // 控制界面刷新的频率
                if (end - start >= Const.PROGRESS_REFRESH_INTERVAL) {
                    progress = currentLength / (totalLength * 1.0f);
                    fileInfo.setProgress(progress);
                    sendMessage(fileInfo, Const.TransferStatus.TRANSFING);
                    start = end;
                }
            }
            bos.flush();
            //文件全部传输完成
            sendMessage(fileInfo, Const.TransferStatus.TRANSFER_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            release();
            sendError(e);
        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void sendMessage(Object obj, int transferStatus) {
        if (obj instanceof FileInfo) {
            ((FileInfo) obj).setFileTransferStatus(transferStatus);
        }
        Message message = mP2pTransferHandler.obtainMessage();
        message.what = transferStatus;
        message.obj = obj;
        mP2pTransferHandler.sendMessage(message);

    }

    @Override
    public void sendError(Exception e) {
        Message message = mP2pTransferHandler.obtainMessage();
        message.what = Const.TransferStatus.TRANSFER_FAILED;
        message.obj = e;
        mP2pTransferHandler.sendMessage(message);
    }

    @Override
    public void release() {
        isStop = true;
        if (mInputStream != null) {
            try {
                mSocketClient.close();
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
