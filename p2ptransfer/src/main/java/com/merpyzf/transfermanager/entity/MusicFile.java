package com.merpyzf.transfermanager.entity;

import android.graphics.Color;

/**
 * Created by wangke on 2017/12/23.
 * 音乐文件
 */

public class MusicFile extends FileInfo {

    // 专辑id
    private long albumId;
    // 作者
    private String artist;
    // 时长
    private long duration;

    public MusicFile() {
    }

    public MusicFile(String name, String path, int type, int length, long albumId, String artist, long duration) {
        super(name, path, type, length, "mp3");
        this.albumId = albumId;
        this.artist = artist;
        this.duration = duration;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
