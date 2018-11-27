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
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.entity.VideoFile;
import com.merpyzf.transfermanager.util.FilePathManager;
import com.merpyzf.transfermanager.util.FormatUtils;
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
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(iv);
            }
            ivFileType.setImageResource(R.drawable.ic_type_image);
        } else if (item instanceof VideoFile) {
            VideoFile videoFile = (VideoFile) item;
            File videoThumb = FilePathManager.getLocalVideoThumbCacheFile(videoFile.getName());
            Glide.with(mContext)
                    .load(videoThumb)
                    .placeholder(R.drawable.ic_holder_video)
                    .error(R.drawable.ic_holder_video)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .centerCrop()
                    .into(iv);
            ivFileType.setImageResource(R.drawable.ic_type_video);
        } else if (item instanceof MusicFile) {
            MusicFile musicFile = (MusicFile) item;
            File albumFile = FilePathManager.getLocalMusicAlbumCacheFile(String.valueOf(musicFile.getAlbumId()));
            //设置封面图片
            Glide.with(mContext)
                    .load(albumFile)
                    .dontAnimate()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_holder_album_art)
                    .error(R.drawable.ic_holder_album_art)
                    .into(iv);
            ivFileType.setImageResource(R.drawable.ic_type_audio);
        }

        helper.setText(R.id.tv_title, item.getName() + "." + item.getSuffix());
        String[] sizeStrArray = com.merpyzf.transfermanager.util.FileUtils.getFileSizeArrayStr(item.getLength());
        helper.setText(R.id.tv_size, sizeStrArray[0] + "" + sizeStrArray[1]);
    }
}
