package com.merpyzf.xmshare.ui.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {


    private int space;

    public RecyclerViewItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        outRect.right = space/2;
        outRect.bottom = space;
        outRect.left = space/2;

        if (parent.getChildLayoutPosition(view) == 0) {
            //outRect.top = space;
        } else {
            outRect.top = 0;

        }

    }
}
