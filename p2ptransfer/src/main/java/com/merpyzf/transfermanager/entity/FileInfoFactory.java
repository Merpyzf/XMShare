package com.merpyzf.transfermanager.entity;

import com.merpyzf.transfermanager.util.FilePathManager;

import java.io.File;

public class FileInfoFactory {


    public static FileInfo convertFileType(FileInfo fileInfo) {

        switch (fileInfo.getType()) {

            case FileInfo.FILE_TYPE_APP:

                ApkFile apkFile = new ApkFile();
                apkFile.setName(fileInfo.getName());
                apkFile.setType(FileInfo.FILE_TYPE_APP);
                apkFile.setLength(fileInfo.getLength());
                apkFile.setThumbLength(fileInfo.getThumbLength());
                apkFile.setSuffix(fileInfo.getSuffix());
                apkFile.setPath(FilePathManager.getSaveAppDir().getPath() + File.separator +
                        fileInfo.getName() + "." + fileInfo.getSuffix());
                apkFile.setMd5(fileInfo.getMd5());
                apkFile.setIsLast(fileInfo.getIsLast());

                return apkFile;

            case FileInfo.FILE_TYPE_IMAGE:
                PicFile picFile = new PicFile();
                picFile.setName(fileInfo.getName());
                picFile.setType(FileInfo.FILE_TYPE_IMAGE);
                picFile.setLength(fileInfo.getLength());
                picFile.setSuffix(fileInfo.getSuffix());
                picFile.setPath(FilePathManager.getSavePhotoDir().getPath() + File.separator +
                        fileInfo.getName() + "." + fileInfo.getSuffix());
                picFile.setMd5(fileInfo.getMd5());
                picFile.setIsLast(fileInfo.getIsLast());

                return picFile;

            case FileInfo.FILE_TYPE_MUSIC:
                MusicFile musicFile = new MusicFile();
                musicFile.setName(fileInfo.getName());
                musicFile.setType(FileInfo.FILE_TYPE_MUSIC);
                musicFile.setLength(fileInfo.getLength());
                musicFile.setThumbLength(fileInfo.getThumbLength());
                musicFile.setSuffix(fileInfo.getSuffix());
                musicFile.setPath(FilePathManager.getSaveMusicDir().getPath() + File.separator +
                        fileInfo.getName() + "." + fileInfo.getSuffix());
                musicFile.setMd5(fileInfo.getMd5());
                musicFile.setIsLast(fileInfo.getIsLast());
                return musicFile;

            case FileInfo.FILE_TYPE_VIDEO:
                VideoFile videoFile = new VideoFile();
                videoFile.setName(fileInfo.getName());
                videoFile.setType(FileInfo.FILE_TYPE_VIDEO);
                videoFile.setLength(fileInfo.getLength());
                videoFile.setThumbLength(fileInfo.getThumbLength());
                videoFile.setSuffix(fileInfo.getSuffix());
                videoFile.setPath(FilePathManager.getSaveVideoDir().getPath() + File.separator +
                        fileInfo.getName() + "." + fileInfo.getSuffix());
                videoFile.setMd5(fileInfo.getMd5());
                videoFile.setIsLast(fileInfo.getIsLast());
                return videoFile;


            default:
                break;

        }

        return null;
    }


}
