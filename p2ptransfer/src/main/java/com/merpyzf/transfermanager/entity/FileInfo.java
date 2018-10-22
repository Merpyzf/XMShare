package com.merpyzf.transfermanager.entity;

import android.content.Context;

import com.github.promeg.pinyinhelper.Pinyin;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.util.FileUtils;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by wangke on 2017/12/23.
 */
public class FileInfo extends DataSupport implements Serializable {

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
     * 文件id
     */
    private String id;
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件类型
     */
    private int type;
    /**
     * 文件的后缀名
     */
    private String suffix;
    /**
     * 中/英文名称对应的第一个字母
     */
    private char firstCase;
    /**
     * 文件大小
     */
    private int length;
    /**
     * 文件缩略图大小
     */
    private int thumbLength;

    byte[] fileThumbArray;
    /**
     * 文件传输的进度
     */
    private float progress;

    /**
     * 文件传输速度
     */
    private String[] transferSpeed;
    /**
     * 文件传输状态
     */
    private int fileTransferStatus = Const.TransferStatus.TRANSFER_WAITING;
    /**
     * 是否是传输中的最后一个文件，如果是则为1，不是默认为0
     */
    private int isLast = 0;
    /**
     * 文件完整性校验
     */
    // md5的值可能获取不到
    private String md5;


    public FileInfo() {
    }

    public FileInfo(String name, String path, int type, int length, String suffix) {
        this.name = name;
        this.firstCase = setFirstCase(name);
        this.path = path;
        this.type = type;
        this.length = length;
        this.suffix = suffix;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.firstCase = setFirstCase(name);
    }

    private char setFirstCase(String name) {
        if (!"".equals(name)) {
            char firstCase = Pinyin.toPinyin(name.charAt(0)).toLowerCase().toCharArray()[0];
            if (firstCase >= 48 && firstCase <= 57) {
                return '#';
            } else {
                return firstCase;
            }
        } else {
            return '#';
        }
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }


    public int getIsLast() {
        return isLast;
    }

    public void setIsLast(int isLast) {
        this.isLast = isLast;
    }

    public int getFileTransferStatus() {
        return fileTransferStatus;
    }

    public void setFileTransferStatus(int fileTransferStatus) {
        this.fileTransferStatus = fileTransferStatus;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String[] getTransferSpeed() {
        return transferSpeed;
    }

    public void setTransferSpeed(String[] transferSpeed) {
        this.transferSpeed = transferSpeed;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getThumbLength() {
        return thumbLength;
    }

    public void setThumbLength(int thumbLength) {
        this.thumbLength = thumbLength;
    }

    public char getFirstCase() {
        return firstCase;
    }

    public byte[] getFileThumbArray() {
        return fileThumbArray;
    }

    public void setFileThumbArray(byte[] fileThumbArray) {
        this.fileThumbArray = fileThumbArray;
    }

    /**
     * 获取传输中的文件头信息
     *
     * @return
     */
    public String getHeader(Context context) {

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
        Header.append(isLast);
        Header.append(Const.S_SEPARATOR);
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

    /**
     * 解析文件头信息
     *
     * @param strHeader
     * @return
     */
    public void decodeHeader(String strHeader) throws Exception {
        String[] split = strHeader.split(Const.S_SEPARATOR);
        this.setType(Integer.valueOf(split[0]));
        this.setName(split[1]);
        this.setSuffix(split[2]);
        this.setLength(Integer.valueOf(split[3]));
        this.setThumbLength(Integer.valueOf(split[4]));
        this.setMd5(split[5]);
        this.setIsLast(Integer.valueOf(split[6]));
    }


    /**
     * 重置当前对象的状态
     */
    public void reset() {
        progress = 0;
        isLast = 0;
        setFileTransferStatus(Const.TransferStatus.TRANSFER_WAITING);
    }

}


