package com.merpyzf.transfermanager.entity;

/**
 * Created by wangke on 2018/1/9.
 * 视频文件
 */

public class VideoFile extends FileInfo {
    // 封面ID
    private String albumId;
    // 视频时长
    private long duration;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }



}
