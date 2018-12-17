package com.merpyzf.transfermanager.entity.factory;

import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.entity.StorageFile;
import com.merpyzf.transfermanager.entity.VideoFile;
import com.merpyzf.common.utils.FilePathManager;

import java.io.File;

/**
 * @author wangke
 */
public class FileInfoFactory {


    public static BaseFileInfo convertFileType(BaseFileInfo fileInfo) {

        switch (fileInfo.getType()) {

            case BaseFileInfo.FILE_TYPE_APP:

                ApkFile apkFile = new ApkFile();
                apkFile.setName(fileInfo.getName());
                apkFile.setType(BaseFileInfo.FILE_TYPE_APP);
                apkFile.setLength(fileInfo.getLength());
                apkFile.setThumbLength(fileInfo.getThumbLength());
                apkFile.setSuffix(fileInfo.getSuffix());
                apkFile.setPath(FilePathManager.getSaveAppDir().getPath() + File.separator +
                        fileInfo.getName() + "." + fileInfo.getSuffix());
                apkFile.setMd5(fileInfo.getMd5());
                apkFile.setIsLast(fileInfo.getIsLast());

                return apkFile;

            case BaseFileInfo.FILE_TYPE_IMAGE:
                PicFile picFile = new PicFile();
                picFile.setName(fileInfo.getName());
                picFile.setType(BaseFileInfo.FILE_TYPE_IMAGE);
                picFile.setLength(fileInfo.getLength());
                picFile.setSuffix(fileInfo.getSuffix());
                picFile.setPath(FilePathManager.getSavePhotoDir().getPath() + File.separator +
                        fileInfo.getName() + "." + fileInfo.getSuffix());
                picFile.setMd5(fileInfo.getMd5());
                picFile.setIsLast(fileInfo.getIsLast());

                return picFile;

            case BaseFileInfo.FILE_TYPE_MUSIC:
                MusicFile musicFile = new MusicFile();
                musicFile.setName(fileInfo.getName());
                musicFile.setType(BaseFileInfo.FILE_TYPE_MUSIC);
                musicFile.setLength(fileInfo.getLength());
                musicFile.setThumbLength(fileInfo.getThumbLength());
                musicFile.setSuffix(fileInfo.getSuffix());
                musicFile.setPath(FilePathManager.getSaveMusicDir().getPath() + File.separator +
                        fileInfo.getName() + "." + fileInfo.getSuffix());
                musicFile.setMd5(fileInfo.getMd5());
                musicFile.setAlbumId(((MusicFile) fileInfo).getAlbumId());
                musicFile.setIsLast(fileInfo.getIsLast());
                return musicFile;

            case BaseFileInfo.FILE_TYPE_VIDEO:
                VideoFile videoFile = new VideoFile();
                videoFile.setName(fileInfo.getName());
                videoFile.setType(BaseFileInfo.FILE_TYPE_VIDEO);
                videoFile.setLength(fileInfo.getLength());
                videoFile.setThumbLength(fileInfo.getThumbLength());
                videoFile.setSuffix(fileInfo.getSuffix());
                videoFile.setPath(FilePathManager.getSaveVideoDir().getPath() + File.separator +
                        fileInfo.getName() + "." + fileInfo.getSuffix());
                videoFile.setMd5(fileInfo.getMd5());
                videoFile.setIsLast(fileInfo.getIsLast());
                return videoFile;

            case BaseFileInfo.FILE_TYPE_STORAGE:
                StorageFile storageFile = new StorageFile();
                storageFile.setName(fileInfo.getName());
                storageFile.setType(BaseFileInfo.FILE_TYPE_STORAGE);
                storageFile.setLength(fileInfo.getLength());
                storageFile.setSuffix(fileInfo.getSuffix());
                storageFile.setPath(FilePathManager.getSaveStorageDir().getPath() + File.separator +
                        fileInfo.getName());
                storageFile.setIsLast(fileInfo.getIsLast());
                return storageFile;
            default:
                break;

        }

        return null;
    }


}
