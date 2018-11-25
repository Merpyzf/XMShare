package com.merpyzf.xmshare.util;

import android.text.TextUtils;

import com.merpyzf.xmshare.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileTypeHelper {

    static ArrayList<String> sDocumentTypes;
    static ArrayList<String> sCompactTypes;
    static ArrayList<String> sPhotoTypes;
    static ArrayList<String> sMusicTypes;
    static ArrayList<String> sVideoTypes;


    static Map<String, Integer> sIcoResMap;

    static {
        sIcoResMap = new HashMap<>();
        // 文档文件类型
        sDocumentTypes = new ArrayList<>();
        sDocumentTypes.add("txt");
        // 为每一种文件的后缀添加一个与之对应的缩略图
        //sIcoResMap.put("txt", null);
        sDocumentTypes.add("doc");
        sDocumentTypes.add("docx");
        sDocumentTypes.add("xls");
        sDocumentTypes.add("xlsx");
        sDocumentTypes.add("wps");
        sDocumentTypes.add("pdf");
        sDocumentTypes.add("pdf");
        sDocumentTypes.add("mobi");
        sDocumentTypes.add("azw");
        sDocumentTypes.add("azw3");
        sDocumentTypes.add("epub");
        sDocumentTypes.add(".key");
        sDocumentTypes.add(".psd");
        sDocumentTypes.add(".html");
        sDocumentTypes.add(".tif");
        sDocumentTypes.add(".caj");

        // 压缩文件类型
        sCompactTypes = new ArrayList<>();
        sCompactTypes.add("zip");
        sCompactTypes.add("rar");
        sCompactTypes.add("tar");
        sCompactTypes.add("tgz");
        sCompactTypes.add("7z");
        sCompactTypes.add("img");
        sCompactTypes.add("iso");

        //图形文件类型
        sPhotoTypes = new ArrayList<>();
        sPhotoTypes.add("jpeg");
        sPhotoTypes.add("jpg");
        sPhotoTypes.add("gif");
        sPhotoTypes.add("png");
        sPhotoTypes.add("bmp");
        sPhotoTypes.add("webp");

        //音乐文件类型
        sMusicTypes = new ArrayList<>();
        sMusicTypes.add("mp3");
        sMusicTypes.add("amr");
        sMusicTypes.add("wav");
        sMusicTypes.add("midi");
        sMusicTypes.add("wma");
        sMusicTypes.add("aac");
        sMusicTypes.add("flac");
        sMusicTypes.add("ac3");
        sMusicTypes.add("mmf");

        // 视频文件类型
        sVideoTypes = new ArrayList<>();
        sVideoTypes.add("avi");
        sVideoTypes.add("wmv");
        sVideoTypes.add("mpeg");
        sVideoTypes.add("mp4");
        sVideoTypes.add("mov");
        sVideoTypes.add("mkv");
        sVideoTypes.add("flv");
        sVideoTypes.add("f4v");
        sVideoTypes.add("m4v");
        sVideoTypes.add("rmvb");
        sVideoTypes.add("rm");
        sVideoTypes.add("3gp");
        sVideoTypes.add("dat");
        sVideoTypes.add("ts");
        sVideoTypes.add("mts");
        sVideoTypes.add("vob");


    }

    /**
     * 通过文件的后缀名获取ico的资源id
     *
     * @param suffix
     * @return
     */
    public static int getIcoResBySuffix(String suffix) {

        if (TextUtils.equals("", suffix)) {
            return R.drawable.ic_fm_other;
        }
        Integer fileIcoRes;
        if (sDocumentTypes.contains(suffix)) {
            fileIcoRes = R.drawable.ic_fm_document;
        } else if (sCompactTypes.contains(suffix)) {
            fileIcoRes = R.drawable.ic_fm_compact;
        } else if (sVideoTypes.contains(suffix)) {
            fileIcoRes = R.drawable.ic_fm_video;
        } else if (sMusicTypes.contains(suffix)) {
            fileIcoRes = R.drawable.ic_fm_music;
        } else if ("ttf".equals(suffix)) {
            fileIcoRes = R.drawable.ic_fm_ttf;
        } else {
            fileIcoRes = R.drawable.ic_fm_other;
        }
        return fileIcoRes;
    }

    /**
     * 根据文件的后缀判断是否是图片类型的文件
     *
     * @param suffix
     * @return
     */
    public static boolean isPhotoType(String suffix) {
        return sPhotoTypes.contains(suffix);
    }
}
