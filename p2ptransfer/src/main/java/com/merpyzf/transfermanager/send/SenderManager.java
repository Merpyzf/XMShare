package com.merpyzf.transfermanager.send;

import android.content.Context;

import com.merpyzf.transfermanager.P2pTransferHandler;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.observer.TransferObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangke on 2018/1/17.
 */

public class SenderManager {

    private ExecutorService mSingleThreadPool;
    private Context mContext;
    private static SenderManager mSenderManager;
    private SenderTaskImp mSenderTaskImp;
    private P2pTransferHandler mP2pTransferHandler;
    // 观察者集合
    private List<TransferObserver> mTransferObserverLists;
    private String TAG = SenderManager.class.getSimpleName();


    public static SenderManager getInstance(Context context) {
        if (mSenderManager == null) {
            synchronized (Object.class) {
                if (mSenderManager == null) {
                    mSenderManager = new SenderManager(context);
                }
            }
        }
        return mSenderManager;
    }


    private SenderManager(Context context) {
        mTransferObserverLists = new ArrayList<>();
        mP2pTransferHandler = new P2pTransferHandler(mTransferObserverLists);
        mSingleThreadPool = Executors.newSingleThreadExecutor();
        this.mContext = context.getApplicationContext();
    }

    /**
     * 注册一个观察者
     *
     * @param transferObserver
     */
    public void register(TransferObserver transferObserver) {
        mTransferObserverLists.add(transferObserver);
    }

    /**
     * 移除一个观察者
     *
     * @param transferObserver
     */
    public void unRegister(TransferObserver transferObserver) {
        if (mTransferObserverLists.contains(transferObserver)) {
            mTransferObserverLists.remove(transferObserver);
        }

    }

    /**
     * 发送文件
     *
     * @param destAddress
     * @param fileInfoList
     */
    public void send(String destAddress, List<FileInfo> fileInfoList) {
        mSenderTaskImp = new SenderTaskImp(mContext, destAddress, fileInfoList, mP2pTransferHandler);
        mSingleThreadPool.execute(mSenderTaskImp);
    }

    /**
     * 传输结束时进行资源的释放
     */
    public void release() {
        if (mSenderTaskImp != null) {
            mSenderTaskImp.release();
        }
    }
}
