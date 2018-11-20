package com.merpyzf.xmshare.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.Volume;
import com.merpyzf.xmshare.util.StorageUtils;

import java.util.List;

public class VolumeAdapter extends BaseQuickAdapter<Volume, BaseViewHolder> {
    private Context mContext;

    public VolumeAdapter(Context context, int layoutResId, @Nullable List<Volume> data) {
        super(layoutResId, data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Volume item) {
        if (item.isRemovable()) {
            helper.setText(R.id.tv_name, "SD卡");
            helper.setImageResource(R.id.iv_volume, R.drawable.ic_sd_card);

            String info = StorageUtils.getAvailableSize(mContext, item.getPath()) + " / "
                    + StorageUtils.getTotalSize(mContext, item.getPath());
            helper.setText(R.id.tv_info, info);
        } else {
            helper.setText(R.id.tv_name, "手机存储");
            helper.setImageResource(R.id.iv_volume, R.drawable.ic_inner_storage);
            String info = StorageUtils.getAvailableSize(mContext, item.getPath()) + " / "
                    + StorageUtils.getTotalSize(mContext, item.getPath());
            helper.setText(R.id.tv_info, info);
        }


    }
}
