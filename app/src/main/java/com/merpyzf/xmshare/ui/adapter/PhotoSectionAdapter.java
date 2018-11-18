package com.merpyzf.xmshare.ui.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.Section;
import com.merpyzf.xmshare.util.UiUtils;

import java.io.IOException;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class PhotoSectionAdapter extends BaseSectionQuickAdapter<Section, BaseViewHolder> {

    public PhotoSectionAdapter(int layoutResId, int sectionHeadResId, List<Section> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, Section item) {
        helper.setText(R.id.tv_selction_head, item.header+" ("+item.getChildNum()+")");
        CheckBox checkBox = helper.getView(R.id.checkBox);
        checkBox.setChecked(item.isCheckedAllChild());
        helper.addOnClickListener(R.id.checkBox);

    }

    @Override
    protected void convert(BaseViewHolder helper, Section item) {
        ImageView iv = helper.getView(R.id.iv_cover);
        ImageView ivSelect = helper.getView(R.id.iv_select);
        GifImageView gifIv = helper.getView(R.id.gif_iv);

        PicFile picFile = (PicFile) item.t;

        String suffix = picFile.getSuffix();
        if ("gif".equals(suffix.toLowerCase())) {

            iv.setVisibility(View.INVISIBLE);
            gifIv.setVisibility(View.VISIBLE);

            try {
                GifDrawable gifDrawable = new GifDrawable(picFile.getPath());
                gifIv.setImageDrawable(gifDrawable);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            iv.setVisibility(View.VISIBLE);
            gifIv.setVisibility(View.INVISIBLE);

            Glide.with(mContext)
                    .load(picFile.getPath())
                    .error(UiUtils.getPlaceHolder(picFile.getType()))
                    .placeholder(UiUtils.getPlaceHolder(picFile.getType()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .into(iv);
        }

        if (App.getTransferFileList().contains(picFile)) {
            ivSelect.setVisibility(View.VISIBLE);

        } else {
            ivSelect.setVisibility(View.INVISIBLE);
        }

    }
}
