package com.merpyzf.xmshare.ui.widget.tools;

import android.support.v7.widget.RecyclerView;

/**
 * @author wangke
 */
public abstract class CustomRecyclerScrollViewListener extends RecyclerView.OnScrollListener {

    private int scrollDist = 0;
    private boolean isVisible = true;
    /**
     * 最小滑动触发距离
     */
    private final float MINIMUM = 20;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // 如果状态可见，并且滑动大于最小滑动距离，就隐藏
        if (isVisible && scrollDist > MINIMUM) {
            hide();
            scrollDist = 0;
            isVisible = false;
        } else if (!isVisible && Math.abs(scrollDist) > MINIMUM) {
            show();
            scrollDist = 0;
            isVisible = true;
        }

        if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
            scrollDist += dy;
        }


    }

    public abstract void show();

    public abstract void hide();
}
