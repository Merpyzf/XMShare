package com.merpyzf.xmshare.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.PhotoDirBean;
import com.merpyzf.xmshare.ui.activity.OnFileSelectListener;

import java.io.IOException;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by merpyzf on 2018/4/2.
 */

public class PhotoDirsAdapter extends BaseQuickAdapter<PhotoDirBean, BaseViewHolder> {

    private OnFileSelectListener mFileSelectListener;
    private CheckBox mCheckBoxAll;

    public PhotoDirsAdapter(int layoutResId, CheckBox checkBoxAll, @Nullable List<PhotoDirBean> data, OnFileSelectListener fileSelectListener) {
        super(layoutResId, data);
        this.mFileSelectListener = fileSelectListener;
        this.mCheckBoxAll = checkBoxAll;
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoDirBean item) {

        helper.setText(R.id.tv_photo_dir_name, item.getName());
        helper.setText(R.id.tv_photo_num, item.getImageNumber() + "张照片");

        ImageView ivCover = helper.getView(R.id.iv_photo_dir_cover);
        GifImageView gifIv = helper.getView(R.id.gif_photo_dir_cover);
        ImageView ivSelect = helper.getView(R.id.iv_select);
        String suffix = FileUtils.getFileSuffix(item.getCoverImg());

        if (item.isChecked()) {
            ivSelect.setImageResource(R.drawable.ic_cb_checked);
        } else {
            ivSelect.setImageResource(R.drawable.ic_cb_unchecked);
        }

        if ("gif".equals(suffix.toLowerCase())) {

            ivCover.setVisibility(View.INVISIBLE);
            gifIv.setVisibility(View.VISIBLE);
            try {
                GifDrawable gifDrawable = new GifDrawable(item.getCoverImg());
                gifIv.setImageDrawable(gifDrawable);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ivCover.setVisibility(View.VISIBLE);
            gifIv.setVisibility(View.INVISIBLE);
            Glide.with(mContext)
                    .load(item.getCoverImg())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .into(ivCover);
        }

        ivSelect.setOnClickListener(v -> {
            if (item.isChecked()) {
                mFileSelectListener.onCancelSelectedAll(item.getImageList());
                ivSelect.setImageResource(R.drawable.ic_cb_unchecked);
            } else {
                mFileSelectListener.onSelectedAll(item.getImageList());
                ivSelect.setImageResource(R.drawable.ic_cb_checked);
            }

            item.setChecked(!item.isChecked());
            mCheckBoxAll.setChecked(isCheckAllDirs());
        });

    }

    private boolean isCheckAllDirs() {

        for (PhotoDirBean photoDirBean : mData) {

            if (!photoDirBean.isChecked()) {
                return false;
            }
        }
        return true;
    }

}
