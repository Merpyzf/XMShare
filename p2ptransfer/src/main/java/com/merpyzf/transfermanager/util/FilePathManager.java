package com.merpyzf.transfermanager.util;

import android.os.Environment;

import java.io.File;

public class FilePathManager {
    public static String ROOT_DIR_NAME = "xmshare";
    /**
     * 文件缓存目录
     */
    public static String CACHE_ROOT_PATH = File.separator + ROOT_DIR_NAME + File.separator + ".cache";
    /**
     * 接收文件保存的根路径
     */
    public static String SAVE_ROOT_PATH = File.separator + ROOT_DIR_NAME + File.separator + "receive";
    /**
     * 接收apk文件的保存路径
     */
    public static String SAVE_APK_PATH = SAVE_ROOT_PATH + File.separator + "apk";
    /**
     * 接收音乐类型文件的保存路径
     */
    public static String SAVE_MUSIC_PATH = SAVE_ROOT_PATH + File.separator + "music";
    /**
     * 接收图片类型文件的保存路径
     */
    public static String SAVE_PHOTO_PATH = SAVE_ROOT_PATH + File.separator + "image";
    /**
     * 接收视频类型文件的保存路径
     */
    public static String SAVE_VIDEO_PATH = SAVE_ROOT_PATH + File.separator + "video";

    /**
     * 接收来自文件管理器中文件的保存路径
     */
    public static String SAVE_OTHER_PATH = SAVE_ROOT_PATH + File.separator + "other";


    public static String LOCAL_MUSIC_ALBUM_CACHE_PATH = CACHE_ROOT_PATH + File.separator + ".local" + File.separator + ".music_album";

    public static String LOCAL_VIDEO_THUMB_CACHE_PATH = CACHE_ROOT_PATH + File.separator + ".local" + File.separator + ".video_album";

    public static String LOCAL_APP_THUMB_CACHE_PATH = CACHE_ROOT_PATH + File.separator + ".local" + File.separator + ".app_album";

    public static String PEER_MUSIC_ALBUM_CACHE_PATH = CACHE_ROOT_PATH + File.separator + ".peer" + File.separator + ".music_album";

    public static String PEER_VIDEO_THUMB_CACHE_PATH = CACHE_ROOT_PATH + File.separator + ".peer" + File.separator + ".video_album";

    public static String PEER_APP_THUMB_CACHE_PATH = CACHE_ROOT_PATH + File.separator + ".peer" + File.separator + ".app_album";


    private FilePathManager() {

    }

    public static File getLocalMusicAlbumCacheDir() {
        File file = new File(Environment.getExternalStorageDirectory(), LOCAL_MUSIC_ALBUM_CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getLocalVideoThumbCacheDir() {
        File file = new File(Environment.getExternalStorageDirectory(), LOCAL_VIDEO_THUMB_CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getLocalAppThumbCacheDir() {
        File file = new File(Environment.getExternalStorageDirectory(), LOCAL_APP_THUMB_CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }


    public static File getPeerMusicAlbumCacheDir() {
        File file = new File(Environment.getExternalStorageDirectory(), PEER_MUSIC_ALBUM_CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getPeerVideoThumbCacheDir() {
        File file = new File(Environment.getExternalStorageDirectory(), PEER_VIDEO_THUMB_CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getPeerAppThumbCacheDir() {
        File file = new File(Environment.getExternalStorageDirectory(), PEER_APP_THUMB_CACHE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }


    public static File getLocalMusicAlbumCacheFile(String thumbName) {
        File file = new File(getLocalMusicAlbumCacheDir(), Md5Utils.getMd5(thumbName));
        return file;
    }

    public static File getLocalVideoThumbCacheFile(String thumbName) {
        File file = new File(getLocalVideoThumbCacheDir(), Md5Utils.getMd5(thumbName));
        return file;
    }

    public static File getLocalAppThumbCacheFile(String thumbName) {
        File file = new File(getLocalAppThumbCacheDir(), Md5Utils.getMd5(thumbName));
        return file;
    }

    public static File getPeerMusicAlbumCacheFile(String thumbName) {
        File file = new File(getPeerMusicAlbumCacheDir(), Md5Utils.getMd5(thumbName));
        return file;
    }

    public static File getPeerVideoThumbCacheFile(String thumbName) {
        File file = new File(getPeerVideoThumbCacheDir(), Md5Utils.getMd5(thumbName));
        return file;
    }

    public static File getPeerAppThumbCacheFile(String thumbName) {
        File file = new File(getPeerAppThumbCacheDir(), Md5Utils.getMd5(thumbName));
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

    public static File getSaveOtherDir() {

        File file = new File(Environment.getExternalStorageDirectory(), SAVE_OTHER_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;


    }
}
