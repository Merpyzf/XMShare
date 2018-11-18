package com.merpyzf.xmshare.bean;

/**
 * 文件对象用于文件浏览器
 *
 * @author wangke
 */
public class FileInfo {

    private String name;
    private String path;
    private long size;
    private boolean isDirectory = false;
    private boolean isPhoto = false;

    public FileInfo(String name, String path, long size, boolean isDirectory, boolean isPhoto) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.isDirectory = isDirectory;
        this.isPhoto = isPhoto;
    }

    public FileInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
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
