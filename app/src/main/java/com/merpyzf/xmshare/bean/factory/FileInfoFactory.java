package com.merpyzf.xmshare.bean.factory;

import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.CompactFile;
import com.merpyzf.transfermanager.entity.DocFile;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.xmshare.bean.model.LitepalFileInfo;
import com.merpyzf.xmshare.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangke
 * @date 2018/4/16
 */

public class FileInfoFactory {

    public static List<FileInfo> toFileInfoList(List<LitepalFileInfo> litepalFileInfos) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        for (LitepalFileInfo litepalFileInfo : litepalFileInfos) {
            fileInfoList.add(toFileInfoType(litepalFileInfo));
        }
        return fileInfoList;
    }

    public static List<LitepalFileInfo> toLitepalFileInfoList(List<FileInfo> fileInfos) {
        List<LitepalFileInfo> litepalFileInfoList = new ArrayList<>();
        for (FileInfo fileInfo : fileInfos) {
            litepalFileInfoList.add(toLitepalFileInfoType(fileInfo));
        }
        return litepalFileInfoList;
    }

    public static FileInfo toFileInfoType(LitepalFileInfo litepalFileInfo) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(litepalFileInfo.getName());
        fileInfo.setPath(litepalFileInfo.getPath());
        fileInfo.setSuffix(litepalFileInfo.getSuffix());
        fileInfo.setLength(litepalFileInfo.getLength());
        fileInfo.setType(litepalFileInfo.getType());
        return fileInfo;
    }

    public static FileInfo toFileInfoType(File file, int fileType) {
        FileInfo fileInfo = new FileInfo();
        switch (fileType) {
            case FileInfo.FILE_TYPE_APP:
                fileInfo.setName(file.getName());
                fileInfo.setPath(file.getPath());
                fileInfo.setLength((int) file.length());
                fileInfo.setType(FileInfo.FILE_TYPE_APP);
                fileInfo.setSuffix(FileUtils.getFileSuffix(file));
                break;
            case FileInfo.FILE_TYPE_MUSIC:
                fileInfo.setName(file.getName());
                fileInfo.setPath(file.getPath());
                fileInfo.setLength((int) file.length());
                fileInfo.setType(FileInfo.FILE_TYPE_MUSIC);
                fileInfo.setSuffix(FileUtils.getFileSuffix(file));
                break;
            case FileInfo.FILE_TYPE_VIDEO:
                fileInfo.setName(file.getName());
                fileInfo.setLength((int) file.length());
                fileInfo.setPath(file.getPath());
                fileInfo.setType(FileInfo.FILE_TYPE_VIDEO);
                fileInfo.setSuffix(FileUtils.getFileSuffix(file));
                break;
            case FileInfo.FILE_TYPE_IMAGE:
                fileInfo.setName(file.getName());
                fileInfo.setPath(file.getPath());
                fileInfo.setLength((int) file.length());
                fileInfo.setType(FileInfo.FILE_TYPE_IMAGE);
                fileInfo.setSuffix(FileUtils.getFileSuffix(file));
                break;
            case FileInfo.FILE_TYPE_OTHER:

                break;
            default:
                break;
        }
        return fileInfo;
    }

    public static LitepalFileInfo toLitepalFileInfoType(FileInfo fileInfo) {
        LitepalFileInfo litepalFileInfo = new LitepalFileInfo();
        litepalFileInfo.setName(fileInfo.getName());
        litepalFileInfo.setPath(fileInfo.getPath());
        litepalFileInfo.setLength(fileInfo.getLength());
        litepalFileInfo.setSuffix(fileInfo.getSuffix());
        litepalFileInfo.setType(fileInfo.getType());
        return litepalFileInfo;
    }

    public static ApkFile toApkFileType(LitepalFileInfo litepalFileInfo) {
        ApkFile apkFile = new ApkFile();
        apkFile.setName(litepalFileInfo.getName());
        apkFile.setPath(litepalFileInfo.getPath());
        apkFile.setSuffix(litepalFileInfo.getSuffix());
        apkFile.setLength(litepalFileInfo.getLength());
        apkFile.setType(litepalFileInfo.getType());
        return apkFile;
    }

    public static CompactFile toCompactFileType(LitepalFileInfo litepalFileInfo) {
        CompactFile compactFile = new CompactFile();
        compactFile.setName(litepalFileInfo.getName());
        compactFile.setPath(litepalFileInfo.getPath());
        compactFile.setSuffix(litepalFileInfo.getSuffix());
        compactFile.setLength(litepalFileInfo.getLength());
        compactFile.setType(litepalFileInfo.getType());
        return compactFile;
    }

    public static DocFile toDocFileType(LitepalFileInfo litepalFileInfo) {
        DocFile docFile = new DocFile();
        docFile.setName(litepalFileInfo.getName());
        docFile.setPath(litepalFileInfo.getPath());
        docFile.setSuffix(litepalFileInfo.getSuffix());
        docFile.setLength(litepalFileInfo.getLength());
        docFile.setType(litepalFileInfo.getType());
        return docFile;
    }

    /**
     * 将app模块下的FileInfo对象转换成FileServer模块下可用的类型
     *
     * @param fileInfo app模块下的FileInfo对象
     * @return FileServer模块下的FileInfo对象
     */
    public static com.merpyzf.fileserver.common.bean.FileInfo toFileServerType(FileInfo fileInfo) {
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

    public static List<com.merpyzf.fileserver.common.bean.FileInfo> toFileServerType(List<FileInfo> fileInfoList) {
        List<com.merpyzf.fileserver.common.bean.FileInfo> serverFileInfoList = new ArrayList<>();
        for (FileInfo fileInfo : fileInfoList) {
            serverFileInfoList.add(toFileServerType(fileInfo));
        }
        return serverFileInfoList;
    }
}
