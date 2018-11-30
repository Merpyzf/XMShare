package com.merpyzf.xmshare.ui.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.merpyzf.xmshare.util.AnimationUtils;
import com.merpyzf.xmshare.util.UiUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import net.qiujuer.genius.res.Resource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by wangke on 2017/12/24.
 */

public class FileAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private Context mContext;
    private List<T> mFileList;
    private Map<Long, Integer> mMusicBottomColorMap = new HashMap<>();
    private static final String TAG = FileAdapter.class.getSimpleName();

    public FileAdapter(Context context, int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
        this.mContext = context;
        this.mFileList = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, final T item) {

        if (item instanceof ApkFile) {
            ApkFile apkFile = (ApkFile) item;
            ImageView imageView = helper.getView(R.id.iv_cover);
            TextView tvApkName = helper.getView(R.id.tv_apk_name);
            TextView tvApkSize = helper.getView(R.id.tv_apk_size);
            if (tvApkName != null) {
                tvApkName.setText(apkFile.getName());
            }
            imageView.setImageDrawable(apkFile.getApkDrawable());
            long length = apkFile.getLength();
            if (tvApkSize != null) {
                tvApkSize.setText(FormatUtils.convert2Mb(length) + "MB");
            }
        } else if (item instanceof MusicFile) {
            MusicFile musicFile = (MusicFile) item;
            long albumId = musicFile.getAlbumId();

            helper.setText(R.id.tv_title, musicFile.getName());
            helper.setText(R.id.tv_info, musicFile.getArtist() + " " + FormatUtils.convert2Mb(musicFile.getLength()) + " MB");
            LinearLayout llBottom = helper.getView(R.id.ll_music_bottom);
            File albumFile = FilePathManager.getLocalMusicAlbumCacheFile(String.valueOf(musicFile.getAlbumId()));
            ImageView imageView = helper.getView(R.id.iv_cover);
            //设置封面图片
            Glide.with(mContext)
                    .load(albumFile)
                    .dontAnimate()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_holder_album_art)
                    .error(R.drawable.ic_holder_album_art)
                    .into(imageView);


            //if (mMusicBottomColorMap == null) {
            //    mMusicBottomColorMap = new HashMap<>();
            //}

            //if (mMusicBottomColorMap.containsKey(albumId)) {
            //    Integer color = mMusicBottomColorMap.get(albumId);
            //    llBottom.setBackgroundColor(color);
            //} else {
            //    try {
            //        Palette palette = Palette.from(BitmapFactory.decodeFile(albumFile.getPath())).generate();
            //        int vibrantColor = palette.getVibrantColor(Resource.Color.BROWN);
            //        llBottom.setBackgroundColor(vibrantColor);
            //        mMusicBottomColorMap.put(albumId, vibrantColor);
            //    } catch (IllegalArgumentException e) {
            //        e.printStackTrace();
            //    }
            //}


        } else if (item instanceof PicFile) {
            ImageView iv = helper.getView(R.id.iv_cover);
            GifImageView gifIv = helper.getView(R.id.gif_iv);

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
            /**
             * 被选中了
             */
            if (App.getTransferFileList().contains(item)) {
                // 缩小
                AnimationUtils.zoomOutCover(iv, 0);
            } else {
                AnimationUtils.zoomInCover(iv, 0);
            }
        } else if (item instanceof VideoFile) {

            VideoFile videoFile = (VideoFile) item;
            String videoName = videoFile.getName();
            helper.setText(R.id.tv_title, videoName);
            helper.setText(R.id.tv_info, FormatUtils.convertMS2Str(videoFile.getDuration()) + " " + FormatUtils.convert2Mb(videoFile.getLength()) + " MB");

            LinearLayout llBottom = helper.getView(R.id.ll_video_bottom);
            ImageView ivVideoThumb = helper.getView(R.id.iv_cover);
            File videoThumb = FilePathManager.getLocalVideoThumbCacheFile(videoName);
            Glide.with(mContext)
                    .load(videoThumb)
                    .placeholder(R.drawable.ic_holder_video)
                    .error(R.drawable.ic_holder_video)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .centerCrop()
                    .into(ivVideoThumb);
        }

        ImageView ivSelect = helper.getView(R.id.iv_select);

        if (App.getTransferFileList().contains(item)) {
            ivSelect.setVisibility(View.VISIBLE);
        } else {
            ivSelect.setVisibility(View.INVISIBLE);
        }

    }


    @NonNull
    @Override
    public String getSectionName(int position) {
        BaseFileInfo fileInfo = (BaseFileInfo) mFileList.get(position);
        return String.valueOf(fileInfo.getFirstLetter());
    }


}
