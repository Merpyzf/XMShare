package com.merpyzf.xmshare.bean.model;


import org.litepal.crud.DataSupport;

public class FileMd5Model extends DataSupport {


    public FileMd5Model() {
    }

    public FileMd5Model(String fileName, String md5) {
        this.fileName = fileName;
        this.md5 = md5;
    }

    private int id;

    private String fileName;

    private String md5;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
