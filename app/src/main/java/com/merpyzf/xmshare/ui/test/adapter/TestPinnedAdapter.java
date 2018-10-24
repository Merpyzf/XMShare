package com.merpyzf.xmshare.ui.test.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.xmshare.ui.test.entity.PinnedEntity;
import com.merpyzf.xmshare.ui.test.entity.PinnedHeaderEntity;
import com.oushangfeng.pinnedsectionitemdecoration.utils.FullSpanUtil;

import java.util.List;

/**
 * Created by merpyzf on 2018/4/19.
 */

public class TestPinnedAdapter extends BaseHeaderAdapter<PinnedHeaderEntity<PicFile>> {


    public TestPinnedAdapter(List<PinnedHeaderEntity<PicFile>> data) {
        super(data);
    }

    @Override
    protected void addItemTypes() {

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        FullSpanUtil.onAttachedToRecyclerView(recyclerView, this, PinnedEntity.TYPE_HEADER);
    }

    @Override
    protected void convert(BaseViewHolder helper, PinnedHeaderEntity<PicFile> item) {

    }


    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        FullSpanUtil.onViewAttachedToWindow(holder, this, PinnedEntity.TYPE_HEADER);
    }
}
