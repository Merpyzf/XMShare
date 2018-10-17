package com.merpyzf.xmshare.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.entity.VideoFile;
import com.merpyzf.transfermanager.util.FormatUtils;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.util.AnimationUtils;
import com.merpyzf.xmshare.util.Md5Utils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by wangke on 2017/12/24.
 */

public class FileAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private Context mContext;
    private List<T> mFileList;
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
            } else {
                Log.i("wkk", "tvApkName 为null");
            }


            imageView.setImageDrawable(apkFile.getApkDrawable());
            int length = apkFile.getLength();
            if (tvApkSize != null) {
                tvApkSize.setText(FormatUtils.convert2Mb(length) + "MB");
            } else {
                Log.i("wkk", "tvApkSize 为null");
            }

        } else if (item instanceof MusicFile) {


            MusicFile musicFile = (MusicFile) item;

            helper.setText(R.id.tv_title, musicFile.getName());
            helper.setText(R.id.tv_artist, musicFile.getArtist());
            helper.setText(R.id.tv_size, FormatUtils.convert2Mb(musicFile.getLength()) + " MB");
            File albumFile = new File(Const.PIC_CACHES_DIR, Md5Utils.getMd5(String.valueOf(musicFile.getAlbumId())));
            ImageView imageView = helper.getView(R.id.iv_cover);
            if (albumFile.exists()) {
                //设置封面图片
                Glide.with(mContext)
                        .load(albumFile)
                        .dontAnimate()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ic_holder_music)
                        .error(R.drawable.ic_holder_music)
                        .into(imageView);


            }


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
                        .error(R.drawable.ic_holder_image)
                        .placeholder(R.drawable.ic_holder_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(iv);
            }

            /**
             * 被选中了
             */
            if (App.getSendFileList().contains(item)) {

                // 缩小
                AnimationUtils.zoomOutCover(iv, 0);

            } else {
                AnimationUtils.zoomInCover(iv, 0);
            }


        } else if (item instanceof VideoFile) {

            VideoFile videoFile = (VideoFile) item;
            helper.setText(R.id.tv_title, videoFile.getName());
            helper.setText(R.id.tv_size, FormatUtils.convert2Mb(videoFile.getLength()) + " MB");
            helper.setText(R.id.tv_duration, FormatUtils.convertMS2Str(videoFile.getDuration()));
            ImageView ivVideoThumb = helper.getView(R.id.iv_cover);
            String videoThumbPath = Const.PIC_CACHES_DIR + "/" + Md5Utils.getMd5(videoFile.getPath());

            Glide.with(mContext)
                    .load(new File(videoThumbPath))
                    .placeholder(R.drawable.ic_holder_video)
                    .error(R.drawable.ic_holder_video)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .centerCrop()
                    .into(ivVideoThumb);


        }

        FileInfo fileInfo = (FileInfo) item;
        ImageView ivSelect = helper.getView(R.id.iv_select);

        if (App.getSendFileList().contains(fileInfo)) {
            ivSelect.setVisibility(View.VISIBLE);

        } else {
            ivSelect.setVisibility(View.INVISIBLE);
        }

    }


    @NonNull
    @Override
    public String getSectionName(int position) {
        FileInfo fileInfo = (FileInfo) mFileList.get(position);
        if (fileInfo instanceof PicFile) {
            return "*_*";
        }
        return String.valueOf(fileInfo.getFirstCase());
    }


}
