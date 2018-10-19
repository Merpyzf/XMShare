package com.merpyzf.xmshare.observer;

import com.merpyzf.transfermanager.entity.FileInfo;

import java.util.List;

public abstract class AbsFileStatusObserver implements FilesStatusObserver {


    @Override
    public void onSelected(FileInfo fileInfo) {

    }

    @Override
    public void onCancelSelected(FileInfo fileInfo) {

    }

    @Override
    public void onSelectedAll(List<FileInfo> fileInfoList) {

    }

    @Override
    public void onCancelSelectedAll(List<FileInfo> fileInfoList) {

    }
}
