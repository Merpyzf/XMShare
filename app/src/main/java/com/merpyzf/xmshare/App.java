package com.merpyzf.xmshare;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.util.SharedPreUtils;
import com.merpyzf.xmshare.util.ToastUtils;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangke on 2018/1/16.
 */

public class App extends LitePalApplication {


    private static Context AppContext;

    /**
     * 待发送的文件集合
     */
    private static List<FileInfo> mTransferFiles;
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
        mTransferFiles = new ArrayList<>();
    }


    /**
     * 从文件集合中添加一个待发送的文件
     *
     * @param fileInfo
     */
    public static void addTransferFile(FileInfo fileInfo) {
        if (fileInfo != null) {
            if (!mTransferFiles.contains(fileInfo)) {
                mTransferFiles.add(fileInfo);
            }
        }
    }


    public static void addTransferFiles(List<FileInfo> fileInfoList) {
        if (fileInfoList != null) {
            for (FileInfo fileInfo : fileInfoList) {
                if (!mTransferFiles.contains(fileInfo)) {
                    mTransferFiles.add(fileInfo);
                }
            }
        }
    }

    public static void removeTransferFiles(List<FileInfo> fileInfoList) {
        if (fileInfoList != null) {
            for (FileInfo fileInfo : fileInfoList) {
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
    public static void removeTransferFile(FileInfo fileInfo) {
        if (fileInfo != null) {
            if (mTransferFiles.contains(fileInfo)) {
                mTransferFiles.remove(fileInfo);
            }
        }
    }

    public static void removeTransferFileByPath(String filePath) {
        if (filePath != null && !"".equals(filePath)) {
            for (int i = 0; i < mTransferFiles.size(); i++) {
                FileInfo transferFile = mTransferFiles.get(i);
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
    public static boolean isContain(com.merpyzf.xmshare.bean.FileInfo fileInfo) {
        if (fileInfo != null) {
            for (FileInfo transferFile : mTransferFiles) {
                if (transferFile.getPath().equals(fileInfo.getPath())) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 返回待发送的文件集合
     *
     * @return
     */
    public static List<FileInfo> getTransferFileList() {
        return mTransferFiles;

    }

    /**
     * 重置待发送文件集合的状态
     */
    public static void resetSelectedFilesStatus() {
        for (int i = 0; i < mTransferFiles.size(); i++) {
            FileInfo fileInfo = mTransferFiles.get(i);
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
