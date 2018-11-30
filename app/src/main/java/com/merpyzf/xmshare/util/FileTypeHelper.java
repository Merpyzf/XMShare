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
    static ArrayList<String> sCodeTypes;


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
        sDocumentTypes.add("torrent");
        sDocumentTypes.add("pages");
        sDocumentTypes.add("key");
        sDocumentTypes.add("numbers");

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

        // 编程语言的源文件后缀
        sCodeTypes = new ArrayList<>();
        sCodeTypes.add("java");
        sCodeTypes.add("jsp");
        sCodeTypes.add("c");
        sCodeTypes.add("cpp");
        sCodeTypes.add("py");
        sCodeTypes.add("php");
        sCodeTypes.add("json");
        sCodeTypes.add("xml");
        sCodeTypes.add("kt");
        sCodeTypes.add("js");
        sCodeTypes.add("h");
        sCodeTypes.add("cs");
    }

    /**
     * 通过文件的后缀名获取ico的资源id
     *
     * @param suffix
     * @return
     */
    public static Integer getIcoResBySuffix(String suffix) {
        if (TextUtils.equals("", suffix)) {
            return R.drawable.ic_fileitem_blank;
        }
        if (sDocumentTypes.contains(suffix)) {
            if ("doc".equals(suffix) || "docx".equals(suffix) || "pages".equals(suffix)) {
                return R.drawable.ic_fileitem_word;
            }
            if ("xls".equals(suffix) || "xlsx".equals(suffix) || "numbers".equals(suffix)) {
                return R.drawable.ic_fileitem_excel;
            }
            if ("pdf".equals(suffix) || "key".equals(suffix)) {
                if ("pdf".equals(suffix)) {
                    return R.drawable.ic_fileitem_pdf;
                }
            }
            if ("html".equals(suffix)) {
                return R.drawable.ic_fileitem_html;
            }

            if ("psd".equals(suffix)) {
                return R.drawable.ic_fileitem_psd;
            }
            if ("txt".equals(suffix)) {
                return R.drawable.ic_fileitem_txt;
            }
            if ("torrent".equals(suffix)) {
                return R.drawable.ic_fileitem_bt;
            }
            return R.drawable.ic_fileitem_document;
        } else if (sCompactTypes.contains(suffix)) {
            if ("iso".equals(suffix)) {
                return R.drawable.ic_fileitem_iso;
            }
            return R.drawable.ic_fileitem_zip;
        } else if (sVideoTypes.contains(suffix)) {
            return R.drawable.ic_fileitem_video;
        } else if (sMusicTypes.contains(suffix)) {
            return R.drawable.ic_fileitem_music;
        } else if ("ttf".equals(suffix)) {
            return R.drawable.ic_fileitem_ttf;
        } else if (sCodeTypes.contains(suffix)) {
            return R.drawable.ic_fileitem_code;
        } else {
            return R.drawable.ic_fileitem_other;
        }
    }

    /**
     * 根据文件的后缀判断是否是图片类型的文件
     *
     * @param suffix
     * @return
     */
    public static boolean isPhotoType(String suffix) {
        return sPhotoTypes.contains(suffix.toLowerCase());
    }
}
