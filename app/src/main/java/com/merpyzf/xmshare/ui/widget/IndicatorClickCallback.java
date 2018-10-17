package com.merpyzf.xmshare.ui.widget;

/**
 * Created by wangke on 2018/3/17.
 * 顶部指示器标签点击的回调接口
 */

public interface IndicatorClickCallback {

    /**
     * 标签被点击的回调方法
     *
     * @param currentPath 当前路径
     * @param isBack      是否返回到上一层
     */
    void onClick(String currentPath, boolean isBack);


}
