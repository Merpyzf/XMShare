package com.merpyzf.xmshare.util;

import android.content.Context;
import android.os.storage.StorageManager;

import com.merpyzf.xmshare.bean.Volume;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


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
                for (int i = 0; i < volumeList.length; i++) {
                    volume = new Volume();
                    volume.setPath((String) volumeList[i].getClass().getMethod("getPath").invoke(volumeList[i]));
                    volume.setRemovable((boolean) volumeList[i].getClass().getMethod("isRemovable").invoke(volumeList[i]));
                    volume.setState((String) volumeList[i].getClass().getMethod("getState").invoke(volumeList[i]));
                    volumes.add(volume);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return volumes;
    }
}
