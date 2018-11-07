package com.merpyzf.xmshare.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.entity.VideoFile;
import com.merpyzf.transfermanager.util.FilePathManager;
import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.util.UiUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by wangke on 2017/12/24.
 */

public class FileSelectAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    private Context mContext;
    private List<T> mFileInfoList;

    public FileSelectAdapter(Context context, int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
        this.mContext = context;
        this.mFileInfoList = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, final T item) {
        helper.addOnLongClickListener(R.id.ll_selected_files);
        helper.addOnClickListener(R.id.iv_remove);
        FileInfo fileInfo = (FileInfo) item;
        String[] fileSizeArray = FileUtils.getFileSizeArrayStr(fileInfo.getLength());
        helper.setText(R.id.tv_title, fileInfo.getName());
        helper.setText(R.id.tv_path, fileInfo.getPath());
        helper.setText(R.id.tv_size, "文件大小:" + fileSizeArray[0] + fileSizeArray[1]);
        ImageView imageView = helper.getView(R.id.iv_file_thumb);
        if (item instanceof ApkFile) {
            ApkFile apkFile = (ApkFile) item;
            imageView.setImageDrawable(apkFile.getApkDrawable());
        } else if (item instanceof MusicFile) {
            MusicFile musicFile = (MusicFile) item;
            File albumFile = FilePathManager.getLocalMusicAlbumCacheFile(String.valueOf(musicFile.getAlbumId()));
            if (albumFile.exists()) {
                //设置封面图片
                Glide.with(mContext)
                        .load(albumFile)
                        .placeholder(UiUtils.getPlaceHolder(fileInfo.getType()))
                        .error(UiUtils.getPlaceHolder(fileInfo.getType()))
                        .crossFade()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageView);
            }
        } else if (item instanceof PicFile) {
            PicFile picFile = (PicFile) item;
            String suffix = FileUtils.getFileSuffix(picFile.getPath()).toLowerCase();
            if ("gif".equals(suffix)) {
                try {
                    GifDrawable gifDrawable = new GifDrawable(picFile.getPath());
                    imageView.setImageDrawable(gifDrawable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Glide.with(mContext)
                        .load(picFile.getPath())
                        .placeholder(UiUtils.getPlaceHolder(fileInfo.getType()))
                        .error(UiUtils.getPlaceHolder(fileInfo.getType()))
                        .crossFade()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageView);
            }
        } else if (item instanceof VideoFile) {
            VideoFile videoFile = (VideoFile) item;
            File videoThumb = FilePathManager.getLocalVideoThumbCacheFile(videoFile.getName());
            Glide.with(mContext)
                    .load(videoThumb)
                    .placeholder(UiUtils.getPlaceHolder(fileInfo.getType()))
                    .error(UiUtils.getPlaceHolder(fileInfo.getType()))
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView);
        }
        ImageView ivRemove = helper.getView(R.id.iv_remove);
    }
}
