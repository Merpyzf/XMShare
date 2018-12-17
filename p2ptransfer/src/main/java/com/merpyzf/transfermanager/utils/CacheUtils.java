package com.merpyzf.transfermanager.utils;

import android.os.Environment;

import com.merpyzf.common.utils.FilePathManager;
import com.merpyzf.common.utils.Md5Utils;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author wangke
 */
public class CacheUtils {

    private CacheUtils() {
        throw new Error("Do not need instantiate!");
    }

    /**
     * 缓存接收到的缩略图到本地
     *
     * @param fileInfo
     */
    public static void cacheReceiveThumb(BaseFileInfo fileInfo, InputStream inputStream) throws IOException {
        if (fileInfo == null || inputStream == null) {
            return;
        }
        boolean isContain = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File destFile = null;
            switch (fileInfo.getType()) {
                case BaseFileInfo.FILE_TYPE_APP:
                    destFile = FilePathManager.getPeerAppThumbCacheFile(fileInfo.getName());
                    isContain = FileUtils.isContain(destFile.getParentFile(), Md5Utils.getMd5(fileInfo.getName()));
                    break;
                case BaseFileInfo.FILE_TYPE_MUSIC:
                    destFile = FilePathManager.getPeerMusicAlbumCacheFile(String.valueOf(((MusicFile) fileInfo).getAlbumId()));
                    isContain = FileUtils.isContain(destFile.getParentFile(), Md5Utils.getMd5(String.valueOf(((MusicFile) fileInfo).getAlbumId())));
                    break;
                case BaseFileInfo.FILE_TYPE_VIDEO:
                    destFile = FilePathManager.getPeerVideoThumbCacheFile(fileInfo.getName());
                    isContain = FileUtils.isContain(destFile.getParentFile(), Md5Utils.getMd5(fileInfo.getName()));
                    break;
                default:
                    break;

            }
            if (isContain) {
                // 等待有足够可读的字节流
                while (inputStream.available() <= fileInfo.getThumbLength()) {
                }
                inputStream.skip(fileInfo.getThumbLength());
            } else {
                FileUtils.writeStream2SdCard(destFile, inputStream, fileInfo.getThumbLength());
            }
        }
    }


}
