package com.merpyzf.xmshare.observer;

import com.merpyzf.transfermanager.entity.FileInfo;

import java.util.List;

/**
 * 监听文件传输列表中的文件是否发生改变的观察者
 */
public interface FilesStatusObserver {
    /**
     * 文件选择时的回调
     *
     * @param fileInfo 选择的文件
     */
    public void onSelected(FileInfo fileInfo);

    /**
     * 文件取消选择的回调
     *
     * @param fileInfo 被取消的文件
     */
    public void onCancelSelected(FileInfo fileInfo);

    /**
     * 全选/取消全选的回调
     */
    public void onSelectedAll(List<FileInfo> fileInfoList);

    /**
     * 取消文件全选
     *
     * @param fileInfoList 取消选择的文件列表
     */
    public void onCancelSelectedAll(List<FileInfo> fileInfoList);
}
