package com.merpyzf.transfermanager.util;

import android.os.Environment;
import android.util.Log;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CacheUtils {


    private CacheUtils() {

    }

    /**
     * 缓存接收到的缩略图到本地
     *
     * @param fileInfo
     */
    public static void cacheReceiveThumb(FileInfo fileInfo, InputStream inputStream) throws IOException {
        if (fileInfo == null || inputStream == null) {
            return;
        }
        boolean isContain = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File destFile = null;
            switch (fileInfo.getType()) {
                case FileInfo.FILE_TYPE_APP:
                    destFile = FilePathManager.getPeerAppThumbCacheFile(fileInfo.getName());
                    isContain = FileUtils.isContain(destFile.getParentFile(), Md5Utils.getMd5(fileInfo.getName()));
                    break;
                case FileInfo.FILE_TYPE_MUSIC:
                    destFile = FilePathManager.getPeerMusicAlbumCacheFile(String.valueOf(((MusicFile) fileInfo).getAlbumId()));
                    isContain = FileUtils.isContain(destFile.getParentFile(), Md5Utils.getMd5(String.valueOf(((MusicFile) fileInfo).getAlbumId())));
                    break;
                case FileInfo.FILE_TYPE_VIDEO:
                    destFile = FilePathManager.getPeerVideoThumbCacheFile(fileInfo.getName());
                    isContain = FileUtils.isContain(destFile.getParentFile(), Md5Utils.getMd5(fileInfo.getName()));
                    break;
                default:
                    break;

            }
            if (isContain) {
                while (inputStream.available() <= fileInfo.getThumbLength()) {
                }
                Log.i("WW2K", fileInfo.getName()+"专辑封面已存在，跳过");

                inputStream.skip(fileInfo.getThumbLength());
            } else {
                Log.i("WW2K", fileInfo.getName()+"专辑封面不存在，缓存");
                FileUtils.writeStream2SdCard(destFile, inputStream, fileInfo.getThumbLength());
            }
        }
    }


}
