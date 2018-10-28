package com.merpyzf.transfermanager.entity;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.util.FileUtils;

/**
 * Created by wangke on 2017/12/23.
 * 音乐文件
 */

public class MusicFile extends FileInfo {

    // 专辑id
    private int albumId;
    // 作者
    private String artist;
    // 时长
    private long duration;

    public MusicFile() {
    }

    public MusicFile(String name, String path, int type, int length, int albumId, String artist, long duration) {
        super(name, path, type, length, "mp3");
        this.albumId = albumId;
        this.artist = artist;
        this.duration = duration;
    }

    public int getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(int albumId) {
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

    @Override
    public String generateHeader(Context context) {

        StringBuilder Header = new StringBuilder();
        //文件类型
        Header.append(this.getType());
        Header.append(Const.S_SEPARATOR);

        //文件名
        Header.append(this.getName());
        Header.append(Const.S_SEPARATOR);

        // 后缀名
        Header.append(this.getSuffix());
        Header.append(Const.S_SEPARATOR);

        // 文件大小
        Header.append(this.getLength());
        Header.append(Const.S_SEPARATOR);

        int fileThumbLength = 0;
        if (this.getType() != FileInfo.FILE_TYPE_IMAGE) {
            // 缩略图大小(耗时操作)
            fileThumbArray = FileUtils.getFileThumbByteArray(context, this);
            fileThumbLength = fileThumbArray.length;
        }
        // 文件缩略图大小
        Header.append(fileThumbLength);
        Header.append(Const.S_SEPARATOR);
        // 文件MD5值
        Header.append(this.getMd5());
        Header.append(Const.S_SEPARATOR);
        //最后一个文件
        Header.append(getIsLast());
        Header.append(Const.S_SEPARATOR);
        Header.append(this.albumId);
        Header.append(Const.S_END);

        int currentLength = Header.toString().getBytes().length;
        if (currentLength < Const.FILE_HEADER_LENGTH) {
            // 少于的部分使用空格填充
            for (int j = 0; j < Const.FILE_HEADER_LENGTH - currentLength; j++) {
                Header.append(Const.FILL_CHARACTER);
            }
        }
        return Header.toString();
    }

    @Override
    public void decodeHeader(String strHeader) throws Exception {
        String[] split = strHeader.split(Const.S_SEPARATOR);
        this.setType(Integer.valueOf(split[0]));
        this.setName(split[1]);
        this.setSuffix(split[2]);
        this.setLength(Integer.valueOf(split[3]));
        this.setThumbLength(Integer.valueOf(split[4]));
        this.setMd5(split[5]);
        this.setIsLast(Integer.valueOf(split[6]));
        this.setAlbumId(Integer.valueOf(split[7]));

        //3:春末的南方城市 2015现场版:mp3:10828068:16839::1:32
    }
}
