package com.merpyzf.transfermanager.entity;

import android.graphics.drawable.Drawable;

/**
 * Created by wangke on 2017/12/24.
 * 应用
 */

public class ApkFile extends FileInfo {

    // 避免对Drawable序列化
    private transient Drawable aplDrawable;

    public ApkFile(String name, String path, int type, int size, Drawable aplDrawable) {
        super(name, path, type, size, "apk");
        this.aplDrawable = aplDrawable;
    }

    public ApkFile() {
    }

    public Drawable getApkDrawable() {
        return aplDrawable;
    }

    public void setApkDrawable(Drawable aplDrawable) {
        this.aplDrawable = aplDrawable;
    }


}
