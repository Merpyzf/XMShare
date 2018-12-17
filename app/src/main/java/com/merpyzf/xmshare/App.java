package com.merpyzf.xmshare;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;

import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author wangke
 * @date 2018/1/16
 */

public class App extends Application {
    private static Context AppContext;
    private static CopyOnWriteArrayList<BaseFileInfo> mTransferFiles;
    private static WifiManager.LocalOnlyHotspotReservation mReservation = null;
    private static String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        //LeakCanary.install(this);
        CrashReport.initCrashReport(getApplicationContext(), "85b38acd34", true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        AppContext = getApplicationContext();
        mTransferFiles = new CopyOnWriteArrayList<>();
    }


    /**
     * 从文件集合中添加一个待发送的文件
     *
     * @param fileInfo
     */
    public static void addTransferFile(BaseFileInfo fileInfo) {
        if (fileInfo != null) {
            if (!mTransferFiles.contains(fileInfo)) {
                mTransferFiles.add(fileInfo);
            }
        }
    }


    public static void addTransferFiles(List<BaseFileInfo> fileInfoList) {
        if (fileInfoList != null) {
            for (BaseFileInfo fileInfo : fileInfoList) {
                if (!mTransferFiles.contains(fileInfo)) {
                    mTransferFiles.add(fileInfo);
                }
            }
        }
    }

    public static void removeTransferFiles(List<BaseFileInfo> fileInfoList) {
        if (fileInfoList != null) {
            for (BaseFileInfo fileInfo : fileInfoList) {
                if (mTransferFiles.contains(fileInfo)) {
                    mTransferFiles.remove(fileInfo);
                }
            }
        }
    }

    /**
     * 从文件集合中移除一个待发送的文件
     *
     * @param fileInfo
     */
    public static void removeTransferFile(BaseFileInfo fileInfo) {
        if (fileInfo != null) {
            if (mTransferFiles.contains(fileInfo)) {
                mTransferFiles.remove(fileInfo);
            }
        }
    }

    public static void removeTransferFileByPath(String filePath) {
        if (filePath != null && !"".equals(filePath)) {
            for (int i = 0; i < mTransferFiles.size(); i++) {
                BaseFileInfo transferFile = mTransferFiles.get(i);
                if (TextUtils.equals(filePath, transferFile.getPath())) {
                    mTransferFiles.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * 判断从设备存储中选择的文件或文件夹是否在待传输文件列表中
     *
     * @param fileInfo
     * @return
     */
    public static boolean isContain(BaseFileInfo fileInfo) {
        return mTransferFiles.contains(fileInfo);
    }


    /**
     * 返回待发送的文件集合
     *
     * @return
     */
    public static List<BaseFileInfo> getTransferFileList() {
        return mTransferFiles;

    }

    /**
     * 重置待发送文件集合的状态
     */
    public static void resetSelectedFilesStatus() {
        for (int i = 0; i < mTransferFiles.size(); i++) {
            BaseFileInfo fileInfo = mTransferFiles.get(i);
            fileInfo.reset();
            if (i == mTransferFiles.size() - 1) {
                fileInfo.setIsLast(1);
            }
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

}
