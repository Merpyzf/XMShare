package com.merpyzf.xmshare.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.CompactFile;
import com.merpyzf.transfermanager.entity.DocFile;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.xmshare.common.Const;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by merpyzf on 2018/4/16.
 * 文件操作相关的工具类
 */

public class FileUtils {

    private static List<FileInfo> mFileList = new ArrayList<>();

    // TODO: 2018/4/16 等待编辑 

    /**
     * 遍历文件夹下面的文件
     *
     * @param path 根目录
     */
    public static List<FileInfo> traverseFolder(String path) {


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
                            docFile.setType(FileInfo.FILE_TYPE_DOCUMENT);

                            mFileList.add(docFile);

                        } else if (isApkType(suffix)) {


                            ApkFile apkFile = new ApkFile();
                            apkFile.setName(file2.getName());
                            apkFile.setPath(file2.getPath());
                            apkFile.setLength((int) file2.length());
                            apkFile.setSuffix(suffix);
                            apkFile.setType(FileInfo.FILE_TYPE_APP);

                            // todo: 扫描完成后需要对apk的ico进行缓存
                            apkFile.setApkDrawable(null);

                            mFileList.add(apkFile);

                        } else if (isCompactType(suffix)) {

                            CompactFile compactFile = new CompactFile();
                            compactFile.setName(file2.getName());
                            compactFile.setPath(file2.getPath());
                            compactFile.setLength((int) file2.length());
                            compactFile.setSuffix(suffix);
                            compactFile.setType(FileInfo.FILE_TYPE_COMPACT);
                            mFileList.add(compactFile);
                        }
                    }
                }
            }
        } else {
            Log.i("wk", "文件不存在!");
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


}
