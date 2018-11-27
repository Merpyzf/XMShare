package com.merpyzf.xmshare.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.CompactFile;
import com.merpyzf.transfermanager.entity.DocFile;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.xmshare.common.Const;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by merpyzf on 2018/4/16.
 * 文件操作相关的工具类
 */

public class FileUtils {


    private static Map<String, String> sMimeTypeMap = new HashMap<>();
    private static List<BaseFileInfo> mFileList = new ArrayList<>();

    static {
        sMimeTypeMap.put("3gp", "video/3gpp");
        sMimeTypeMap.put("apk", "application/vnd.android.package-archive");
        sMimeTypeMap.put("asf", "video/x-ms-asf");
        sMimeTypeMap.put("avi", "video/x-msvideo");
        sMimeTypeMap.put("bin", "application/octet-stream");
        sMimeTypeMap.put("bin", "application/octet-stream");
        sMimeTypeMap.put("bmp", "image/bmp");
        sMimeTypeMap.put("c", "text/plain");
        sMimeTypeMap.put("conf", "text/plain");
        sMimeTypeMap.put("cpp", "text/plain");
        sMimeTypeMap.put("doc", "application/msword");
        sMimeTypeMap.put("exe", "application/octet-stream");
        sMimeTypeMap.put("gif", "image/gif");
        sMimeTypeMap.put("gtar", "application/x-gtar");
        sMimeTypeMap.put("h", "text/plain");
        sMimeTypeMap.put("htm", "text/html");
        sMimeTypeMap.put("html", "text/html");
        sMimeTypeMap.put("jar", "application/java-archive");
        sMimeTypeMap.put("java", "text/plain");
        sMimeTypeMap.put("jpeg", "image/jpeg");
        sMimeTypeMap.put("jpg", "image/jpeg");
        sMimeTypeMap.put("js", "application/x-javascript");
        sMimeTypeMap.put("log", "text/plain");
        sMimeTypeMap.put("m3u", "audio/x-mpegurl");
        sMimeTypeMap.put("m4a", "audio/mp4a-latm");
        sMimeTypeMap.put("m4b", "audio/mp4a-latm");
        sMimeTypeMap.put("m4p", "audio/mp4a-latm");
        sMimeTypeMap.put("m4v", "video/x-m4v");
        sMimeTypeMap.put("mpc", "application/vnd.mpohun.certificate");
        sMimeTypeMap.put("mpe", "video/mpeg");
        sMimeTypeMap.put("mp3", "audio/x-mpeg");
        sMimeTypeMap.put("mp4", "video/mp4");
        sMimeTypeMap.put("mpeg", "video/mpeg");
        sMimeTypeMap.put("mpg", "video/mpeg");
        sMimeTypeMap.put("mpg4", "video/mp4");
        sMimeTypeMap.put("mpga", "audio/mpeg");
        sMimeTypeMap.put("ogg", "audio/ogg");
        sMimeTypeMap.put("pdf", "application/pdf");
        sMimeTypeMap.put("png", "image/png");
        sMimeTypeMap.put("pps", "application/vnd.ms-powerpoint");
        sMimeTypeMap.put("ppt", "application/vnd.ms-powerpoint");
        sMimeTypeMap.put("prop", "text/plain");
        sMimeTypeMap.put("rar", "application/x-rar-compressed");
        sMimeTypeMap.put("rc", "text/plain");
        sMimeTypeMap.put("rmvb", "audio/x-pn-realaudio");
        sMimeTypeMap.put("rtf", "application/rtf");
        sMimeTypeMap.put("sh", "text/plain");
        sMimeTypeMap.put("tar", "application/x-tar");
        sMimeTypeMap.put("tgz", "application/x-compressed");
        sMimeTypeMap.put("txt", "text/plain");
        sMimeTypeMap.put("wav", "audio/x-wav");
        sMimeTypeMap.put("wma", "audio/x-ms-wma");
        sMimeTypeMap.put("wmv", "audio/x-ms-wmv");
        sMimeTypeMap.put("wps", "application/vnd.ms-works");
        sMimeTypeMap.put("xml", "text/plain");
        sMimeTypeMap.put("z", "application/x-compress");
        sMimeTypeMap.put("zip", "application/zip");
    }

    // TODO: 2018/4/16 等待编辑

    /**
     * 遍历文件夹下面的文件
     *
     * @param path 根目录
     */
    public static List<BaseFileInfo> traverseFolder(String path) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                return null;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        traverseFolder(file2.getAbsolutePath());
                    } else {
                        // 获取文件的后缀
                        String suffix = getFileSuffix(file2.getPath());
                        if (isDocumentType(suffix)) {
                            // TODO: 2018/4/16 扫描完成后，对于md5值进行异步获取
                            DocFile docFile = new DocFile();
                            docFile.setName(file2.getName());
                            docFile.setPath(file2.getPath());
                            docFile.setLength((int) file2.length());
                            docFile.setSuffix(suffix);
                            docFile.setType(BaseFileInfo.FILE_TYPE_DOCUMENT);
                            mFileList.add(docFile);

                        } else if (isApkType(suffix)) {
                            ApkFile apkFile = new ApkFile();
                            apkFile.setName(file2.getName());
                            apkFile.setPath(file2.getPath());
                            apkFile.setLength((int) file2.length());
                            apkFile.setSuffix(suffix);
                            apkFile.setType(BaseFileInfo.FILE_TYPE_APP);
                            // todo: 扫描完成后需要对apk的ico进行缓存
                            apkFile.setApkDrawable(null);
                            mFileList.add(apkFile);
                        } else if (isCompactType(suffix)) {
                            CompactFile compactFile = new CompactFile();
                            compactFile.setName(file2.getName());
                            compactFile.setPath(file2.getPath());
                            compactFile.setLength((int) file2.length());
                            compactFile.setSuffix(suffix);
                            compactFile.setType(BaseFileInfo.FILE_TYPE_COMPACT);
                            mFileList.add(compactFile);
                        }
                    }
                }
            }
        }
        return mFileList;
    }

    /**
     * 判断文件是否为文稿
     *
     * @param suffix 文件后缀名
     * @return
     */
    public static boolean isDocumentType(String suffix) {

        String s = suffix.toLowerCase();

        if (Const.FILE_DOCUMENT_TYPES.contains(s)) {
            return true;
        }

        return false;
    }

    /**
     * 判断文件是否为压缩文件类型
     *
     * @param suffix
     * @return
     */
    public static boolean isCompactType(String suffix) {

        String s = suffix.toLowerCase();

        if (Const.FILE_COMPACT_TYPES.contains(s)) {
            return true;
        }

        return false;
    }

    /**
     * 判断文件是否为应用安装包
     *
     * @param suffix
     * @return
     */
    public static boolean isApkType(String suffix) {

        String s = suffix.toLowerCase();

        if ("apk".equals(s)) {
            return true;
        }
        return false;
    }


    /**
     * 根据文件路径获取文件后缀名
     *
     * @param filePath
     * @return 当获取不到时返回 ""
     */
    public static String getFileSuffix(String filePath) {
        if (filePath == null) {
            return null;
        }
        int beginIndex = filePath.lastIndexOf(".") + 1;
        if (beginIndex == 0) {
            return "";
        }
        return filePath.substring(beginIndex);

    }


    /**
     * 根据File对象获取文件后缀名
     *
     * @param
     * @return 当获取不到时返回 ""
     */
    public static String getFileSuffix(File file) {
        String filePath = file.getPath();
        int beginIndex = filePath.lastIndexOf(".") + 1;
        if (beginIndex == 0) {
            return "";
        }
        return filePath.substring(beginIndex);
    }


    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 添加一个文件到MediaStore媒体数据库中
     *
     * @param file 要添加的文件
     */
    public static void addFileToMediaStore(Context context, File file) {

        Uri contentUri = Uri.fromFile(file);
        // 发送广播，通知系统将此文件添加到数据库中
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        context.sendBroadcast(intent);

    }

    public static boolean isContain(File parent, String fileName) {
        String[] fileNames = parent.list();
        for (String childFileName : fileNames) {
            if (fileName.equals(childFileName)) {
                return true;
            }
        }
        return false;
    }

    public static void openFile(Activity context, File file) {

        if (context == null || file == null) {
            return;
        }
        String suffix = FileUtils.getFileSuffix(file);
        String mimeType = sMimeTypeMap.get(suffix.toLowerCase());
        if ("".equals(mimeType)) {
            mimeType = "*/*";
        }

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = "com.merpyzf.xmshare.receiver_provider";
            Uri contentUri = FileProvider.getUriForFile(context, authority, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.setDataAndType(contentUri, mimeType);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file),
                    mimeType);

        }
        context.startActivity(intent);


    }
}
