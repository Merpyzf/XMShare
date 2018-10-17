package com.merpyzf.transfermanager.entity;

/**
 * Created by wangke on 2018/1/9.
 * 图片文件
 */

public class PicFile extends FileInfo {

    public PicFile(String name, String path, int type, int size) {
        super(name, path, type, size,"png");
        // TODO: 2018/1/9 需要考虑文件后缀名的作用，

    }

    public PicFile() {
    }
}
