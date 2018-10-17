package com.merpyzf.xmshare.util;

import android.os.Build;

/**
 * Created by wangke on 2018/1/31.
 */

public class DeviceUtils {

    /**
     * 获取设备名称
     *
     * @return
     */
    public static String getDeviceName() {
        return Build.MODEL;
    }

}
