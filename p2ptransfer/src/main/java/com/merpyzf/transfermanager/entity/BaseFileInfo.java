package com.merpyzf.transfermanager.entity;

import android.content.Context;

import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.util.FileUtils;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 基类文件对象
 *
 * @author wangke
 * @date 2017/12/23
 */
public class BaseFileInfo extends DataSupport implements Serializable {

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
     * 其他文件 (传输的是文件或文件夹之类的)
     */
    public static final int FILE_TYPE_STORAGE = 0x07;


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
    private char firstLetter;
    /**
     * 文件大小
     */
    private long length;
    /**
     * 文件缩略图大小
     */
    private int thumbLength = 0;

    /**
     * 存放文件缩略图的字节数组
     */
    byte[] thumbBytes;
    /**
     * 记录文件的传输进度
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

    private String lastModified;
    /**
     * 是否是传输中的最后一个文件，如果是则为1，不是默认为0
     */
    private int isLast = 0;
    /**
     * 文件完整性校验
     */
    private String md5;


    public BaseFileInfo() {
    }

    public BaseFileInfo(String name, String path, int type, int length, String suffix) {
        this.name = name;
        this.firstLetter = FileUtils.getFirstLetter(name);
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
        this.firstLetter = FileUtils.getFirstLetter(name);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        this.suffix = FileUtils.getFileSuffix(path);
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

    public char getFirstLetter() {
        return firstLetter;
    }

    public byte[] getThumbBytes() {
        return thumbBytes;
    }

    public void setThumbBytes(byte[] thumbBytes) {
        this.thumbBytes = thumbBytes;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }


    public boolean isLastFile() {
        return this.isLast == Const.IS_LAST;
    }

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
        if (this.type != BaseFileInfo.FILE_TYPE_IMAGE || this.type != BaseFileInfo.FILE_TYPE_STORAGE) {
            // 缩略图大小(耗时操作)
            thumbBytes = FileUtils.getFileThumbByteArray(context, this);
            fileThumbLength = thumbBytes.length;
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

    @Override
    public boolean equals(Object obj) {
        //通过文件所处的路径和文件的带下来判断两者是否是同一个对象
        if (obj instanceof BaseFileInfo) {
            BaseFileInfo fileInfo = (BaseFileInfo) obj;
            if (fileInfo.getPath().equals(this.path) && fileInfo.getLength() == this.getLength()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 重置
     */
    public void reset() {
        progress = 0;
        isLast = 0;
        setFileTransferStatus(Const.TransferStatus.TRANSFER_WAITING);
    }
}


