package com.merpyzf.xmshare.ui.widget;

import android.view.View;

import com.merpyzf.xmshare.ui.widget.bean.Indicator;


/**
 * Created by merpyzf on 2018/3/17.
 */

public interface FileSelectIndicator {

    /**
     * 添加一个指示器
     *
     * @param indicator
     */
    void addIndicator(Indicator indicator);


    /**
     * 移除给定指示器之后的所有的指示器
     *
     * @param indicator
     */
    void removeAfter(Indicator indicator);

    /**
     * 移除给定索引之后的所有指示器
     *
     * @param index
     */
    void removeAfterByIndex(int index);

    /**
     * 移除最后面的那个指示器
     */
    boolean pop();

    /**
     * 是否返回到根标签了
     *
     * @return
     */
    boolean isInRoot();


    /**
     * 创建指示器View
     *
     * @param indicator
     * @return
     */
    View createIndicatorView(Indicator indicator);


}
