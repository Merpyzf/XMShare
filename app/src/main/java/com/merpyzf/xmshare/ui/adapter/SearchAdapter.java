package com.merpyzf.xmshare.ui.adapter;

import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.entity.VideoFile;
import com.merpyzf.transfermanager.util.FilePathManager;
import com.merpyzf.transfermanager.util.FormatUtils;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.util.FileUtils;
import com.merpyzf.xmshare.util.UiUtils;

import net.qiujuer.genius.res.Resource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 2018/11/27
 *
 * @author wangke
 */
public class SearchAdapter extends BaseQuickAdapter<BaseFileInfo, BaseViewHolder> {

    public SearchAdapter(int layoutResId, @Nullable List<BaseFileInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseFileInfo item) {
        helper.setText(R.id.tv_title, item.getName());
        ImageView ivFileType = helper.getView(R.id.iv_file_type);
        ImageView ivSelect = helper.getView(R.id.iv_select);
        ImageView iv = helper.getView(R.id.iv_file_thumb);
        if (item instanceof PicFile) {
            GifImageView gifIv = helper.getView(R.id.gif_file_thumb);
            PicFile picFile = (PicFile) item;
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
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(iv);
            }
            ivFileType.setImageResource(R.drawable.ic_type_image);
        } else if (item instanceof VideoFile) {
            VideoFile videoFile = (VideoFile) item;
            File videoThumb = FilePathManager.getLocalVideoThumbCacheFile(videoFile.getName());
            Glide.with(mContext)
                    .load(videoThumb)
                    .error(UiUtils.getPlaceHolder(videoFile.getType()))
                    .placeholder(UiUtils.getPlaceHolder(videoFile.getType()))
                    .dontAnimate()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(iv);
            ivFileType.setImageResource(R.drawable.ic_type_video);
        } else if (item instanceof MusicFile) {
            MusicFile musicFile = (MusicFile) item;
            File albumFile = FilePathManager.getLocalMusicAlbumCacheFile(String.valueOf(musicFile.getAlbumId()));
            Glide.with(mContext)
                    .load(albumFile)
                    .dontAnimate()
                    .centerCrop()
                    .error(UiUtils.getPlaceHolder(musicFile.getType()))
                    .placeholder(UiUtils.getPlaceHolder(musicFile.getType()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(iv);
            ivFileType.setImageResource(R.drawable.ic_type_audio);
        } else if (item instanceof ApkFile) {
            ApkFile apkFile = (ApkFile) item;
            File thumbFile = FilePathManager.getLocalAppThumbCacheFile(apkFile.getName());
            Glide.with(mContext)
                    .load(thumbFile)
                    .dontAnimate()
                    .centerCrop()
                    .error(UiUtils.getPlaceHolder(apkFile.getType()))
                    .placeholder(UiUtils.getPlaceHolder(apkFile.getType()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(iv);
            ivFileType.setImageResource(R.drawable.ic_type_app);
        }

        helper.setText(R.id.tv_title, item.getName() + "." + item.getSuffix());
        String[] sizeStrArray = com.merpyzf.transfermanager.util.FileUtils.getFileSizeArrayStr(item.getLength());
        helper.setText(R.id.tv_size, sizeStrArray[0] + "" + sizeStrArray[1]);
        if (App.isContain(item)) {
            ivSelect.setVisibility(View.VISIBLE);
        } else {
            ivSelect.setVisibility(View.INVISIBLE);
        }

    }
}
