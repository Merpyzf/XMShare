package com.merpyzf.xmshare.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.format.Formatter;

import com.merpyzf.transfermanager.utils.FileUtils;
import com.merpyzf.xmshare.bean.Volume;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


/**
 * @author wangke
 */
public class StorageUtils {
    /**
     * 获取设备中所有的存储器
     *
     * @param context
     * @return
     */
    public static ArrayList<Volume> getVolumes(Context context) {
        ArrayList<Volume> volumes = new ArrayList<>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getVolumeListMethod = StorageManager.class.getMethod("getVolumeList");
            getVolumeListMethod.setAccessible(true);
            Object[] volumeList = (Object[]) getVolumeListMethod.invoke(storageManager);
            if (volumeList != null) {
                Volume volume;
                for (Object aVolumeList : volumeList) {
                    volume = new Volume();
                    volume.setPath((String) aVolumeList.getClass().getMethod("getPath").invoke(aVolumeList));
                    volume.setRemovable((boolean) aVolumeList.getClass().getMethod("isRemovable").invoke(aVolumeList));
                    volume.setState((String) aVolumeList.getClass().getMethod("getState").invoke(aVolumeList));
                    volumes.add(volume);
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return volumes;
    }

    public static String getStoreInfo(String volumePath) {

        long availableSize = getAvailableSize(volumePath);
        long totalSize = getTotalSize(volumePath);
        String[] blockSizeArray = FileUtils.getFileSizeArrayStr(totalSize);
        String[] availSizeArray = FileUtils.getFileSizeArrayStr(availableSize);
        return "可用 " + availSizeArray[0] + availSizeArray[1] + " / " + blockSizeArray[0] + blockSizeArray[1];
    }

    public static long getAvailableSize(String path) {
        StatFs statFs = new StatFs(path);
        statFs.restat(path);
        return statFs.getAvailableBlocks() * statFs.getBlockSize();
    }

    public static long getTotalSize(String path) {
        StatFs statFs = new StatFs(path);
        statFs.restat(path);
        return statFs.getBlockCount() * statFs.getBlockSize();
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public static String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static String getSDAvailableSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    public static String getTotalSize(Context context, String volumePath) {
        StatFs stat = new StatFs(volumePath);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public static String getAvailableSize(Context context, String volumePath) {
        StatFs stat = new StatFs(volumePath);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

}
