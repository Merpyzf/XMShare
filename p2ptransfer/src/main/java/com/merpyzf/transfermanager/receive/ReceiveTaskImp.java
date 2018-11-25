package com.merpyzf.transfermanager.receive;

import android.content.Context;
import android.os.Message;

import com.merpyzf.transfermanager.P2pTransferHandler;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.entity.StorageFile;
import com.merpyzf.transfermanager.entity.factory.FileInfoFactory;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.util.CacheUtils;
import com.merpyzf.transfermanager.util.CloseUtils;
import com.merpyzf.transfermanager.util.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wangke
 * @date 2017/12/22
 */

public class ReceiveTaskImp implements Runnable, ReceiveTask {

    private Socket mSocketClient;
    private InputStream mInputStream;
    private P2pTransferHandler mP2pTransferHandler;
    private List<BaseFileInfo> mReceiveFileList;
    private Context mContext;
    private boolean isStop = false;
    private static final String TAG = ReceiveTaskImp.class.getSimpleName();

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
     *
     * @throws Exception
     */
    @Override
    public synchronized void receiveTransferList() throws Exception {
        mReceiveFileList = new ArrayList<>();
        while (true) {
            BaseFileInfo fileInfo = decodeFileHeader(mInputStream);
            if (null != fileInfo) {
                fileInfo = FileInfoFactory.convertFileType(fileInfo);
                //接收缩略图到本地，当前设定只有图片不接受缩略图
                receiveThumbToLocal(fileInfo);
                mReceiveFileList.add(fileInfo);
                if (fileInfo.getIsLast() == Const.IS_LAST) {
                    break;
                }
            }
        }
        sendMessage(mReceiveFileList, Const.TransferStatus.TRANSFER_FILE_LIST_SUCCESS);
    }

    @Override
    public synchronized void run() {
        try {
            init();
            //接收待传输文件列表
            receiveTransferList();
            //接收文件
            receiveFiles();
        } catch (Exception e) {
            e.printStackTrace();
            sendError(e);
            exit();
        } finally {
            exit();
        }
    }

    @Override
    public void receiveFiles() throws Exception {
        int receiveIndex = 0;
        while (!isStop) {
            BaseFileInfo fileInfo = mReceiveFileList.get(receiveIndex++);
            receiveBody(fileInfo);
            if (fileInfo.isLastFile()) {
                break;
            }
        }
    }

    /**
     * 接收文件的缩略图存储到本地
     *
     * @param fileInfo
     */
    private void receiveThumbToLocal(BaseFileInfo fileInfo) throws IOException {
        // 照片不用缓存缩略图
        if (fileInfo instanceof PicFile) {
        } else if (fileInfo instanceof StorageFile) {
        } else {
            CacheUtils.cacheReceiveThumb(fileInfo, mInputStream);
        }
    }

    /**
     * 解析文件头信息
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    private BaseFileInfo decodeFileHeader(InputStream inputStream) throws Exception {
        byte[] buffer = new byte[Const.FILE_HEADER_LENGTH];
        int available = inputStream.available();
        if (available >= Const.FILE_HEADER_LENGTH) {
            int readLen = inputStream.read(buffer, 0, Const.FILE_HEADER_LENGTH);
            if (readLen != Const.FILE_HEADER_LENGTH) {
                throw new Exception("读取到文件的头信息出错");
            }
            String strHeader = new String(buffer, Const.S_CHARSET);
            strHeader = trimLastFillChars(strHeader);
            int fileType = Integer.valueOf(strHeader.split(Const.S_SEPARATOR)[0]);
            BaseFileInfo fileInfo;
            if (fileType == BaseFileInfo.FILE_TYPE_MUSIC) {
                fileInfo = new MusicFile();
            } else {
                fileInfo = new BaseFileInfo();
            }
            fileInfo.decodeHeader(strHeader);
            return fileInfo;
        }
        return null;
    }

    /**
     * 去除文件头信息中有用的字符序列
     *
     * @param strHeader
     * @return
     */
    private String trimLastFillChars(String strHeader) {
        return strHeader.substring(0, strHeader.indexOf(Const.S_END));
    }

    /**
     * 进行文件部分字节的读取
     *
     * @param fileInfo
     */
    @Override
    public synchronized void receiveBody(BaseFileInfo fileInfo) {
        // 读取文件的总长度
        long totalLength = fileInfo.getLength();
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
                long leftLength = totalLength - currentLength;
                if (leftLength >= Const.BUFFER_LENGTH) {
                    readLength = mInputStream.read(buffer, 0, Const.BUFFER_LENGTH);
                } else {
                    readLength = mInputStream.read(buffer, 0, (int) leftLength);
                }
                bos.write(buffer, 0, readLength);
                currentLength += readLength;
                perSecondReadLength += readLength;
                end = System.currentTimeMillis();
                endTime = System.currentTimeMillis();
                if (endTime - startTime >= 1000) {
                    String[] transferSpeed = FileUtils.getFileSizeArrayStr(perSecondReadLength);
                    fileInfo.setTransferSpeed(transferSpeed);
                    perSecondReadLength = 0;
                    startTime = endTime;
                }
                if (end - start >= Const.PROGRESS_REFRESH_INTERVAL) {
                    progress = currentLength / (totalLength * 1.0f);
                    fileInfo.setProgress(progress);
                    sendMessage(fileInfo, Const.TransferStatus.TRANSFING);
                    start = end;
                }
            }
            bos.flush();
            fileInfo.setFileTransferStatus(Const.TransferStatus.TRANSFER_SUCCESS);
            sendMessage(fileInfo, Const.TransferStatus.TRANSFER_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            exit();
            sendError(e);
        } finally {
            CloseUtils.close(bos);
        }

    }

    @Override
    public void sendMessage(Object obj, int transferStatus) {
        if (obj instanceof BaseFileInfo) {
            ((BaseFileInfo) obj).setFileTransferStatus(transferStatus);
        }
        Message message = mP2pTransferHandler.obtainMessage();
        message.what = transferStatus;
        message.obj = obj;
        mP2pTransferHandler.sendMessage(message);

    }

    @Override
    public void sendError(Exception e) {
        Message message = mP2pTransferHandler.obtainMessage();
        message.what = Const.TransferStatus.TRANSFER_EXPECTION;
        message.obj = e;
        mP2pTransferHandler.sendMessage(message);
    }

    @Override
    public void exit() {
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
