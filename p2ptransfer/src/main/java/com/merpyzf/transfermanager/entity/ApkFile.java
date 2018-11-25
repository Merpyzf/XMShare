package com.merpyzf.transfermanager.entity;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 *
 * @author wangke
 * @date 2017/12/24
 * 应用
 */

public class ApkFile extends BaseFileInfo {

    /**
     * 避免对Drawable序列化
     */
    private transient Drawable appIcoDrawable;

    public ApkFile(String name, String path, int type, int size, Drawable aplDrawable) {
        super(name, path, type, size, "apk");
        this.appIcoDrawable = aplDrawable;
    }

    public ApkFile() {
    }

    public Drawable getApkDrawable() {
        return appIcoDrawable;
    }

    public void setApkDrawable(Drawable aplDrawable) {
        this.appIcoDrawable = aplDrawable;
    }


}
