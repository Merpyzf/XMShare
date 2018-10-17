package com.merpyzf.transfermanager.send;

import android.content.Context;

import com.merpyzf.transfermanager.P2pTransferHandler;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.interfaces.TransferObserver;

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
    private List<TransferObserver> mTransferObservers;
    private static final String TAG = SenderManager.class.getSimpleName();

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
        mTransferObservers = new ArrayList<>();
        mP2pTransferHandler = new P2pTransferHandler(mTransferObservers);
        mSingleThreadPool = Executors.newSingleThreadExecutor();
        this.mContext = context;
    }

    /**
     * 注册一个观察者
     *
     * @param transferObserver
     */
    public void register(TransferObserver transferObserver) {
        mTransferObservers.add(transferObserver);
    }

    /**
     * 移除指定的观察者
     *
     * @param transferObserver
     */
    public void unRegister(TransferObserver transferObserver) {
        if (mTransferObservers.contains(transferObserver)) {
            mTransferObservers.remove(transferObserver);
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
            // TODO: 2018/10/15 考虑要将下面资源释放的代码移除，传输完成后直接由SenderTaskImp对象内部释放
            mSenderTaskImp.release();
        }
    }

}
