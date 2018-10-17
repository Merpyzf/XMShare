package com.merpyzf.xmshare;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.util.SharedPreUtils;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangke on 2018/1/16.
 */

public class App extends LitePalApplication {


    private static Context AppContext;

    /**
     * 待发送的文件集合
     */
    private static List<FileInfo> mSendFileList;
    private static String TAG = App.class.getSimpleName();
    private static WifiManager.LocalOnlyHotspotReservation mReservation = null;

    @Override
    public void onCreate() {
        super.onCreate();
//        LeakCanary.install(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        AppContext = getApplicationContext();
        mSendFileList = new ArrayList<>();
    }


    /**
     * 从文件集合中添加一个待发送的文件
     *
     * @param fileInfo
     */
    public static void addSendFile(FileInfo fileInfo) {

        if (!mSendFileList.contains(fileInfo)) {
            mSendFileList.add(fileInfo);
        }
    }


    public static void addSendFiles(List<FileInfo> fileInfoList) {

        for (FileInfo fileInfo : fileInfoList) {
            if (!mSendFileList.contains(fileInfo)) {
                mSendFileList.add(fileInfo);
            }
        }
    }


    public static void removeSendFiles(List<FileInfo> fileInfoList) {

        for (FileInfo fileInfo : fileInfoList) {

            if (mSendFileList.contains(fileInfo)) {
                mSendFileList.remove(fileInfo);
            }

        }

    }


    /**
     * 从文件集合中移除一个待发送的文件
     *
     * @param fileInfo
     */
    public static void removeSendFile(FileInfo fileInfo) {

        if (mSendFileList.contains(fileInfo)) {
            mSendFileList.remove(fileInfo);
            //
        }
    }

    /**
     * 返回待发送的文件集合
     *
     * @return
     */
    public static List<FileInfo> getSendFileList() {

        return mSendFileList;

    }

    /**
     * 重置待发送文件集合的状态
     */
    public static void resetSendFileList() {

        for (int i = 0; i < mSendFileList.size(); i++) {
            FileInfo fileInfo = mSendFileList.get(i);
            fileInfo.reset();
        }
    }
    public static Context getAppContext() {
        return AppContext;
    }
    public static void setReservation(WifiManager.LocalOnlyHotspotReservation reservation) {
        mReservation = reservation;
    }

    public static void closeHotspotOnAndroidO() {
        if (mReservation != null) {
            mReservation.close();
        }
    }


    public static WifiManager.LocalOnlyHotspotReservation getReservation() {
        return mReservation;
    }

    /**
     * 获取用户昵称
     *
     * @return
     */
    public static String getNickname() {
        return SharedPreUtils.getString(AppContext, Const.SP_USER, "nickName", "");
    }

}
