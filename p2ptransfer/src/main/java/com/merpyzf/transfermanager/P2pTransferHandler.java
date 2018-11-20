package com.merpyzf.transfermanager;


import android.os.Handler;
import android.os.Message;

import com.github.promeg.pinyinhelper.Pinyin;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.observer.TransferObserver;
import com.merpyzf.transfermanager.receive.ReceiverManager;

import java.util.List;

/**
 * Created by wangke on 2018/1/22.
 */

public class P2pTransferHandler extends Handler {
    private List<TransferObserver> mTransferObservers;
    private ReceiverManager.TransferFileListListener mTransferFileListListener;

    public P2pTransferHandler(List<TransferObserver> transferObserverLists) {
        this.mTransferObservers = transferObserverLists;
    }

    public void setTransferFileListListener(ReceiverManager.TransferFileListListener transferFileListListener) {
        this.mTransferFileListListener = transferFileListListener;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            /**
             * 待接收文件列表传输完毕
             */
            case Const.TransferStatus.TRANSFER_FILE_LIST_SUCCESS:
                List<FileInfo> mReceiveFileList = (List<FileInfo>) msg.obj;
                if (mTransferFileListListener != null && mReceiveFileList.size() > 0) {
                    // 当接收到待传输文件列表时的回调
                    mTransferFileListListener.onReceiveCompleted(mReceiveFileList);
                }
                break;
            /**
             * 传输中
             */
            case Const.TransferStatus.TRANSFING:
                for (int i = 0; i < mTransferObservers.size(); i++) {
                    mTransferObservers.get(i).onTransferProgress((FileInfo) msg.obj);
                }
                break;
            /**
             * 传输成功
             */
            case Const.TransferStatus.TRANSFER_SUCCESS:
                for (int i = 0; i < mTransferObservers.size(); i++) {
                    mTransferObservers.get(i).onTransferStatus((FileInfo) msg.obj);
                }
                break;
            /**
             * 传输失败
             */
            case Const.TransferStatus.TRANSFER_EXPECTION:
                // 错误信息
                Exception exception = (Exception) msg.obj;
                exception.printStackTrace();
                for (int i = 0; i < mTransferObservers.size(); i++) {
                    mTransferObservers.get(i).onTransferError("传输错误！log: " + exception.getMessage());
                }
                break;

            default:
                break;
        }
    }
}
