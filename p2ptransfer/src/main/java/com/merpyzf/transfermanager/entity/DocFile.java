package com.merpyzf.transfermanager.entity;

/**
 *
 * @author wangke
 * @date 2018/4/16
 * 文稿类型
 */

public class DocFile extends BaseFileInfo {

    public DocFile() {

    }

    public DocFile(String name, String path, int type, int length, String suffix) {
        super(name, path, type, length, suffix);
    }
}
