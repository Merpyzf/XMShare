package com.merpyzf.xmshare.bean.factory;

import android.util.Log;

import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.entity.StorageFile;
import com.merpyzf.transfermanager.entity.VideoFile;
import com.merpyzf.xmshare.util.FileTypeHelper;
import com.merpyzf.xmshare.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangke
 * @date 2018/4/16
 */

public class FileInfoFactory {


    public static BaseFileInfo toFileInfoType(File file, int fileType) {
        BaseFileInfo fileInfo = null;
        switch (fileType) {
            case BaseFileInfo.FILE_TYPE_APP:
                fileInfo = new ApkFile();
                fileInfo.setName(file.getName());
                fileInfo.setPath(file.getPath());
                fileInfo.setLength((int) file.length());
                fileInfo.setType(BaseFileInfo.FILE_TYPE_APP);
                fileInfo.setSuffix(FileUtils.getFileSuffix(file));
                break;
            case BaseFileInfo.FILE_TYPE_MUSIC:
                fileInfo = new MusicFile();
                fileInfo.setName(file.getName());
                fileInfo.setPath(file.getPath());
                fileInfo.setLength((int) file.length());
                fileInfo.setType(BaseFileInfo.FILE_TYPE_MUSIC);
                fileInfo.setSuffix(FileUtils.getFileSuffix(file));
                break;
            case BaseFileInfo.FILE_TYPE_VIDEO:
                fileInfo = new VideoFile();
                fileInfo.setName(file.getName());
                fileInfo.setLength((int) file.length());
                fileInfo.setPath(file.getPath());
                fileInfo.setType(BaseFileInfo.FILE_TYPE_VIDEO);
                fileInfo.setSuffix(FileUtils.getFileSuffix(file));
                break;
            case BaseFileInfo.FILE_TYPE_IMAGE:
                fileInfo = new PicFile();
                fileInfo.setName(file.getName());
                fileInfo.setPath(file.getPath());
                fileInfo.setLength((int) file.length());
                fileInfo.setType(BaseFileInfo.FILE_TYPE_IMAGE);
                fileInfo.setSuffix(FileUtils.getFileSuffix(file));
                break;
            case BaseFileInfo.FILE_TYPE_STORAGE:
                fileInfo = new StorageFile();
                fileInfo.setName(file.getName());
                fileInfo.setLength(file.length());
                ((StorageFile) fileInfo).setPath(file.getPath());
                String suffix = ((StorageFile) fileInfo).getSuffix();
                Log.i("ww2k", "suffix: " + suffix);
                ((StorageFile) fileInfo).setPhoto(FileTypeHelper.isPhotoType(suffix));
                fileInfo.setType(BaseFileInfo.FILE_TYPE_STORAGE);

                break;
            default:
                break;
        }
        return fileInfo;
    }

    /**
     * 将app模块下的FileInfo对象转换成FileServer模块下可用的类型
     *
     * @param fileInfo app模块下的FileInfo对象
     * @return FileServer模块下的FileInfo对象
     */
    public static com.merpyzf.fileserver.common.bean.FileInfo toFileServerType(BaseFileInfo fileInfo) {
        com.merpyzf.fileserver.common.bean.FileInfo serverFileInfo = new com.merpyzf.fileserver.common.bean.FileInfo();
        if (fileInfo instanceof MusicFile) {
            serverFileInfo.setAlbumId(((MusicFile) fileInfo).getAlbumId());
        }
        if (fileInfo instanceof PicFile) {
            String fileName = fileInfo.getName().split("\\.")[0];
            serverFileInfo.setName(fileName);
        } else {
            serverFileInfo.setName(fileInfo.getName());
        }
        serverFileInfo.setLength(fileInfo.getLength());
        serverFileInfo.setPath(fileInfo.getPath());
        serverFileInfo.setType(fileInfo.getType());
        return serverFileInfo;
    }

    public static List<com.merpyzf.fileserver.common.bean.FileInfo> toFileServerType(List<BaseFileInfo> fileInfoList) {
        List<com.merpyzf.fileserver.common.bean.FileInfo> serverFileInfoList = new ArrayList<>();
        for (BaseFileInfo fileInfo : fileInfoList) {
            serverFileInfoList.add(toFileServerType(fileInfo));
        }
        return serverFileInfoList;
    }
}
