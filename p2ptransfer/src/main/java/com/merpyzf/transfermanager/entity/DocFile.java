package com.merpyzf.transfermanager.entity;

/**
 * Created by merpyzf on 2018/4/16.
 * 文稿类型
 */

public class DocFile extends FileInfo {

    public DocFile() {
    }

    public DocFile(String name, String path, int type, int length, String suffix) {
        super(name, path, type, length, suffix);
    }
}
