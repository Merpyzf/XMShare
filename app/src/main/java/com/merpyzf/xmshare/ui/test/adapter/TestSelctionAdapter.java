package com.merpyzf.xmshare.ui.test.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.Section;

import java.util.List;

public class TestSelctionAdapter extends BaseSectionQuickAdapter<Section, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param layoutResId      The layout resource id of each item.
     * @param sectionHeadResId The section head layout id for each item
     * @param data             A new list is created out of this one to avoid mutable list
     */
    public TestSelctionAdapter(int layoutResId, int sectionHeadResId, List<Section> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, Section item) {
        helper.setText(R.id.tv_selction_head, item.header);
        CheckBox checkBox = helper.getView(R.id.checkBox);
        checkBox.setChecked(item.isCheckedAllChild());
        helper.addOnClickListener(R.id.checkBox);


    }

    @Override
    protected void convert(BaseViewHolder helper, Section item) {
        helper.setText(R.id.tv_title, item.t.getName());
        ImageView ivSelect = helper.getView(R.id.iv_select);

        if (App.getTransferFileList().contains(item.t)) {
            ivSelect.setVisibility(View.VISIBLE);
        } else {
            ivSelect.setVisibility(View.INVISIBLE);
        }

    }
}
