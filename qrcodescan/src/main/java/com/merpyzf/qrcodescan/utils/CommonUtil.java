package com.merpyzf.qrcodescan.utils;

import android.hardware.Camera;

/**
 * @className: CommonUtil
 * @classDescription: 通用工具类
 * @author: miao
 * @createTime: 2017年2月10日
 */
public class CommonUtil {

    /**
     * @param
     * @return
     * @author miao
     * @createTime 2017年2月10日
     * @lastModify 2017年2月10日
     */
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            canUse = false;
        }
        if (canUse) {
            if (mCamera != null) {
                mCamera.release();
            }
            mCamera = null;
        }
        return canUse;
    }
}
