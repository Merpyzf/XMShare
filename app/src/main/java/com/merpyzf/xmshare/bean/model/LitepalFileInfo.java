package com.merpyzf.xmshare.bean.model;

import com.merpyzf.transfermanager.entity.FileInfo;

import org.litepal.crud.DataSupport;

/**
 * Created by merpyzf on 2018/4/16.
 * 用于适配Litepal数据库
 */

public class LitepalFileInfo extends DataSupport {


    private String name;
    private String path;
    private int length;
    private String suffix;
    private int type;

    public LitepalFileInfo(String name, String path, int length, String suffix, int type) {
        this.name = name;
        this.path = path;
        this.length = length;
        this.suffix = suffix;
        this.type = type;
    }


    public LitepalFileInfo() {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

