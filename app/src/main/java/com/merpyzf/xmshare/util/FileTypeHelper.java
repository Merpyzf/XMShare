package com.merpyzf.xmshare.util;

import com.merpyzf.xmshare.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileTypeHelper {

    static ArrayList<String> sDocumentTypes;
    static ArrayList<String> sCompactTypes;
    static ArrayList<String> sPhotoTypes;
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

    }

    /**
     * 通过文件的后缀名获取ico的资源id
     * @param suffix
     * @return
     */
    public static int getIcoResBySuffix(String suffix){
        int otherFileIcoRes = R.drawable.ic_other;
        Integer fileIcoRes = sIcoResMap.get(suffix);
        if(fileIcoRes == null){
            return otherFileIcoRes;
        }
        return fileIcoRes;
    }

    /**
     * 根据文件的后缀判断是否是图片类型的文件
     * @param suffix
     * @return
     */
    public static boolean isPhotoType(String suffix){
        return sPhotoTypes.contains(suffix);
    }
}
