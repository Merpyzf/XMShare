package com.merpyzf.transfermanager.entity;

import com.merpyzf.transfermanager.utils.FileUtils;

/**
 *
 * @author wangke
 * @date 2018/1/9
 * 图片文件
 */

public class PicFile extends BaseFileInfo {
    String lastChanged;
    public PicFile(String name, String path, int type, int size) {
        super(name, path, type, size,FileUtils.getFileSuffix(path));
    }

    public PicFile() {
    }

    public String getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(String lastChanged) {
        this.lastChanged = lastChanged;
    }
}
