package com.merpyzf.xmshare.ui.activity;

import com.merpyzf.transfermanager.entity.FileInfo;

import java.util.List;

/**
 * Created by wangke on 2018/1/15.
 * 文件选择的监听回调
 */

public interface OnFileSelectListener<T> {

    /**
     * 选择文件
     *
     * @param fileInfo 文件对象
     */
    void onSelected(T fileInfo);

    /**
     * 取消选择文件
     *
     * @param fileInfo 文件对象
     */
    void onCancelSelected(T fileInfo);

    /**
     * 全选
     *
     * @param fileInfoList 选择的文件集合
     */
    void onSelectedAll(List<FileInfo> fileInfoList);

    /**
     * 取消全选
     *
     * @param fileInfoList 取消全选的文件集合
     */
    void onCancelSelectedAll(List<FileInfo> fileInfoList);


}
