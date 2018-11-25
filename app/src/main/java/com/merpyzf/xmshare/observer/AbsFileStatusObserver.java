package com.merpyzf.xmshare.observer;

import com.merpyzf.transfermanager.entity.BaseFileInfo;

import java.util.List;

public abstract class AbsFileStatusObserver implements FilesStatusObserver {


    @Override
    public void onSelected(BaseFileInfo fileInfo) {

    }

    @Override
    public void onCancelSelected(BaseFileInfo fileInfo) {

    }

    @Override
    public void onSelectedAll(List<BaseFileInfo> fileInfoList) {

    }

    @Override
    public void onCancelSelectedAll(List<BaseFileInfo> fileInfoList) {

    }
}
