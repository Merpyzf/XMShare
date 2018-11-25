package com.merpyzf.fileserver.common.bean;

import com.yanzhenjie.andserver.util.FileUtils;

import java.io.File;
import java.io.Serializable;

/**
 * @author wangke
 * @date 2017/12/23
 */
public class FileInfo implements Serializable {


    /**
     * 应用类型(APK)
     */

    public static final int FILE_TYPE_APP = 0x01;

    /**
     * 图片类型
     */
    public static final int FILE_TYPE_IMAGE = 0x02;

    /**
     * 音乐类型
     */
    public static final int FILE_TYPE_MUSIC = 0x03;

    /**
     * 视频类型
     */
    public static final int FILE_TYPE_VIDEO = 0x04;

    /**
     * 文档类型
     */
    public static final int FILE_TYPE_DOCUMENT = 0x05;

    /**
     * 压缩文件
     */
    public static final int FILE_TYPE_COMPACT = 0x06;

    /**
     * 其他文件 (传输的文件夹之类的)
     */
    public static final int FILE_TYPE_OTHER = 0x07;


    /**
     * 文件名
     */
    private String name;
    /**
     * 路径
     */
    private String path;
    /**
     * 文件类型
     */
    private int type;
    /**
     * 文件后缀名
     */
    private String suffix;
    /**
     * 文件大小
     */
    private long length;

    private long albumId;


    public FileInfo() {
    }

    public FileInfo(String name, String path, int type, long length, String suffix) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.length = length;
        this.suffix = suffix;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    /**
     * 获取文件的后缀名
     *
     * @return null: 未能成功获取
     */
    public String getSuffix() {
        if (path == null) {
            return null;
        } else {
            return getFileSuffix(path);
        }
    }

    /**
     * 根据文件路径名获取文件后缀
     *
     * @param path 文件路径
     * @return 文件后缀，获取失败时返回null
     */
    private String getFileSuffix(String path) {
        int beginIndex = path.lastIndexOf(".") + 1;
        if (beginIndex == 0) {
            return "";
        }
        return path.substring(beginIndex);
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

}


