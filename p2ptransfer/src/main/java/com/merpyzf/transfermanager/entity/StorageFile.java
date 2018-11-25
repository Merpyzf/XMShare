package com.merpyzf.transfermanager.entity;

/**
 * 描述本地存储设备中的文件
 *
 * @author wangke
 */
public class StorageFile extends BaseFileInfo {
    private boolean isDirectory = false;
    private boolean isPhoto = false;

    public StorageFile() {
        setType(BaseFileInfo.FILE_TYPE_STORAGE);
    }

    public StorageFile(String name, String path, long length, boolean isDirectory, boolean isPhoto) {
        setName(name);
        setPath(path);
        setLength(length);
        this.isDirectory = isDirectory;
        this.isPhoto = isPhoto;
        setType(BaseFileInfo.FILE_TYPE_STORAGE);
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public boolean isPhoto() {
        return isPhoto;
    }

    public void setPhoto(boolean photo) {
        isPhoto = photo;
    }
}
