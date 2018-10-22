package com.merpyzf.transfermanager.receive;


import android.content.Context;
import android.util.Log;

import com.merpyzf.transfermanager.P2pTransferHandler;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.observer.TransferObserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangke on 2017/12/12.
 * 负责处理文件的接收
 */
public class ReceiverManager implements Runnable {

    private ServerSocket mServerSocket;
    private ExecutorService mSingleThreadPool;
    private Socket mSocketClient;
    private P2pTransferHandler mP2pTransferHandler;
    private static ReceiverManager mReceiver;
    // 观察者集合
    private List<TransferObserver> mTransferObserverLists;
    private ReceiveTaskImp mReceiveTaskImp;
    private static final String TAG = ReceiverManager.class.getSimpleName();
    private static boolean isStop = false;
    private Context mContext;

    /**
     * 单例获取实例
     *
     * @return
     */
    public static ReceiverManager getInstance(Context context) {

        if (mReceiver == null) {
            synchronized (Object.class) {
                if (mReceiver == null) {

                    mReceiver = new ReceiverManager(context);

                }
            }
        }
        reset();
        return mReceiver;
    }


    private ReceiverManager(Context context) {

        this.mContext = context;
        mTransferObserverLists = new ArrayList<>();
        mP2pTransferHandler = new P2pTransferHandler(mTransferObserverLists);
        mSingleThreadPool = Executors.newSingleThreadExecutor();


    }

    /**
     * 注册一个观察者
     *
     * @param transferObserver
     */
    public void register(TransferObserver transferObserver) {

        if (transferObserver != null && !mTransferObserverLists.contains(transferObserver)) {
            mTransferObserverLists.add(transferObserver);
        }

    }
    /**
     * 接触注册一个观察者
     *
     * @param transferObserver
     */
    public void unRegister(TransferObserver transferObserver) {
        if (transferObserver != null && mTransferObserverLists.contains(transferObserver)) {
            mTransferObserverLists.remove(transferObserver);
        }
    }

    public void setOnTransferFileListListener(TransferFileListListener transferFileListListener) {
        if (transferFileListListener != null) {
            mP2pTransferHandler.setTransferFileListListener(transferFileListListener);
        }
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket();
            //是否复用未完全关闭的地址端口
            mServerSocket.setReuseAddress(true);
            mServerSocket.setReceiveBufferSize(64 * 1024 * 1024);
            mServerSocket.setPerformancePreferences(2, 1, 2);
            mServerSocket.bind(new InetSocketAddress(Const.SOCKET_PORT));
            while (!isStop) {
                Log.i(TAG, "SocketServer 阻塞中,等待设备连接....");
                mSocketClient = mServerSocket.accept();
                mReceiveTaskImp = new ReceiveTaskImp(mContext, mSocketClient, mP2pTransferHandler);
                mSingleThreadPool.execute(mReceiveTaskImp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 传输文件列表监听接口
     */
    public interface TransferFileListListener {

        /**
         * 接收待传输文件列表完成的回调
         *
         * @param transferFileList
         */
        void onReceiveCompleted(List<FileInfo> transferFileList);

    }

    /**
     * 重置
     */
    private static void reset() {
        isStop = false;
    }

    /**
     * 释放资源
     */
    public void release() {
        isStop = true;
        if (mReceiveTaskImp != null) {
            mReceiveTaskImp.release();
        }
    }
}
