package com.merpyzf.fileserver.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.merpyzf.fileserver.R;
import com.merpyzf.transfermanager.util.FilePathManager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangke
 */
public class FileTypeHelper {

    static ArrayList<String> sDocumentTypes;
    static ArrayList<String> sCompactTypes;
    static ArrayList<String> sPhotoTypes;
    static ArrayList<String> sMusicTypes;
    static ArrayList<String> sVideoTypes;
    static ArrayList<String> sCodeTypes;
    private static final String ROOT_PATH = "img/file_type/";


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
    public static File getFileTypeThumbBySuffix(Context context, String suffix) {
        String fileTypeName = getFileTypeName(suffix);
        File fileTypeThumb = new File(FilePathManager.getAssentFileTypeThumbPath(), fileTypeName);
        Log.i("ww2k", "fileTypePath: " + fileTypeThumb.getPath());
        if (fileTypeThumb.exists()) {
            Log.i("ww2k", "存在: " + fileTypeThumb.getPath());
            // 如果已经存在则直接使用
            return fileTypeThumb;
        } else {
            //    将assets中的文件拷贝到设备内然后返回
            InputStream in = FileUtils.OpenFileFromAssets(context, ROOT_PATH + fileTypeName);
            IOUtils.writeStreamToFile(in, fileTypeThumb);
        }
        return fileTypeThumb;
    }

    private static String getFileTypeName(String suffix) {
        if (TextUtils.equals("", suffix)) {
            return "file_blank.png";
        }
        if (sDocumentTypes.contains(suffix)) {
            if ("doc".equals(suffix) || "docx".equals(suffix) || "pages".equals(suffix)) {
                return "file_word.png";
            }
            if ("xls".equals(suffix) || "xlsx".equals(suffix) || "numbers".equals(suffix)) {
                return "file_excel.png";
            }
            if ("ppt".equals(suffix) || "key".equals(suffix)) {
                return "file_ppt.png";
            }
            if ("pdf".equals(suffix)) {
                return "file_pdf.png";
            }
            if ("html".equals(suffix)) {
                return "file_html.png";
            }
            if ("psd".equals(suffix)) {
                return "file_psd.png";
            }
            if ("txt".equals(suffix)) {
                return "file_txt.png";
            }
            if ("torrent".equals(suffix)) {
                return "file_bt.png";
            }
            return "file_document.png";
        } else if (sCompactTypes.contains(suffix)) {
            if ("iso".equals(suffix)) {
                return "file_iso.png";
            }
            return "file_zip.png";
        } else if (sVideoTypes.contains(suffix)) {
            return "file_video.png";
        } else if (sMusicTypes.contains(suffix)) {
            return "file_music.png";
        } else if ("ttf".equals(suffix)) {
            return "file_ttf.png";
        } else if (sCodeTypes.contains(suffix)) {
            return "file_code.png";
        } else {
            return "file_other.png";
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
