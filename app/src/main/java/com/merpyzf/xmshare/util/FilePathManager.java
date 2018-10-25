package com.merpyzf.xmshare.util;

import android.os.Environment;

import java.io.File;

public class FilePathManager {
    public static String ROOT_DIR_NAME = "xmshare";
    /**
     * 缩略图缓存目录
     */
    public static String CACHE_ROOT_PATH = File.separator + ROOT_DIR_NAME + File.separator + ".cache";
    /**
     * 接收文件保存的根路径
     */
    public static String SAVE_ROOT_PATH = File.separator + ROOT_DIR_NAME + File.separator + "receive";
    /**
     * 接收apk文件的保存路径
     */
    public static String SAVE_APK_PATH = SAVE_ROOT_PATH + ROOT_DIR_NAME + File.separator + "apk";
    /**
     * 接收音乐类型文件的保存路径
     */
    public static String SAVE_MUSIC_PATH = SAVE_ROOT_PATH + ROOT_DIR_NAME + File.separator + "receive" + File.separator + "music";
    /**
     * 接收音乐类型文件的保存路径
     */
    public static String SAVE_PHOTO_PATH = SAVE_ROOT_PATH + ROOT_DIR_NAME + File.separator + "receive" + File.separator + "image";
    /**
     * 接收音乐类型文件的保存路径
     */
    public static String SAVE_VIDEO_PATH = SAVE_ROOT_PATH + ROOT_DIR_NAME + File.separator + "receive" + File.separator + "video";

    public static String MUSIC_ALBUM_CACHE_PATH = CACHE_ROOT_PATH + File.separator + ".music_album";

    public static String VIDEO_THUMB_CACHE_PATH = CACHE_ROOT_PATH + File.separator + ".video_album";

    public static String APP_THUMB_CACHE_PATH = CACHE_ROOT_PATH + File.separator + ".app_album";

    private FilePathManager() {

    }

    public static File getMusicAlbumCacheDir() {
        File file = new File(Environment.getExternalStorageDirectory(), MUSIC_ALBUM_CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getVideoThumbCacheDir() {
        File file = new File(Environment.getExternalStorageDirectory(), VIDEO_THUMB_CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getAppThumbCacheDir() {
        File file = new File(Environment.getExternalStorageDirectory(), APP_THUMB_CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getSaveAppDir() {

        File file = new File(Environment.getExternalStorageDirectory(), SAVE_APK_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    public static File getSaveMusicDir() {

        File file = new File(Environment.getExternalStorageDirectory(), SAVE_MUSIC_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        String path = file.getPath();
        return file;
    }

    public static File getSavePhotoDir() {

        File file = new File(Environment.getExternalStorageDirectory(), SAVE_PHOTO_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getSaveVideoDir() {

        File file = new File(Environment.getExternalStorageDirectory(), SAVE_VIDEO_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }
}
