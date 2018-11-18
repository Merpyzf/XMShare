package com.merpyzf.xmshare.ui.widget;

import com.merpyzf.xmshare.ui.widget.bean.Indicator;

/**
 * Created by wangke on 2018/3/17.
 * 顶部指示器标签点击的回调接口
 */

public interface IndicatorChangedCallback {

    /**
     * 当指示器发生变化时回调
     *
     * @param indicator
     */
    void onIndicatorChanged(Indicator indicator);


}
