package com.merpyzf.xmshare.ui.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.merpyzf.xmshare.util.AnimationUtils;
import com.merpyzf.xmshare.util.FilePathManager;
import com.merpyzf.xmshare.util.Md5Utils;
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
    private Map<Long, Integer> mAlbumColorMap = new HashMap<>();
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
            long albumId = musicFile.getAlbumId();

            helper.setText(R.id.tv_title, musicFile.getName());
            helper.setText(R.id.tv_artist, musicFile.getArtist());
            helper.setText(R.id.tv_size, FormatUtils.convert2Mb(musicFile.getLength()) + " MB");
            LinearLayout llBottom = helper.getView(R.id.ll_music_bottom);
            File albumFile = new File(FilePathManager.getMusicAlbumCacheDir(), albumId + ".png");
            ImageView imageView = helper.getView(R.id.iv_cover);
            if (albumFile.exists()) {
                //设置封面图片
                Glide.with(mContext)
                        .load(albumFile)
                        .dontAnimate()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ic_default_album_art)
                        .error(R.drawable.ic_default_album_art)
                        .into(imageView);
            }

            if (mAlbumColorMap == null) {
                mAlbumColorMap = new HashMap<>();
            }

            if (mAlbumColorMap.containsKey(albumId)) {
                Integer color = mAlbumColorMap.get(albumId);
                llBottom.setBackgroundColor(color);
                Log.i("WW2k", "直接设置");
            } else {
                try {
                    Palette palette = Palette.from(BitmapFactory.decodeFile(albumFile.getPath())).generate();
                    int vibrantColor = palette.getVibrantColor(Resource.Color.BROWN);
                    llBottom.setBackgroundColor(vibrantColor);
                    mAlbumColorMap.put(albumId, vibrantColor);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Log.i("WW3k", musicFile.getName() + "的封面取色出现异常");
                }
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
            File videoThumb = new File(FilePathManager.getVideoThumbCacheDir(), Md5Utils.getMd5(videoFile.getPath()));
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

        if (App.getSendFileList().contains(item)) {
            ivSelect.setVisibility(View.VISIBLE);

        } else {
            ivSelect.setVisibility(View.INVISIBLE);
        }

    }


    @NonNull
    @Override
    public String getSectionName(int position) {
        FileInfo fileInfo = (FileInfo) mFileList.get(position);
        return String.valueOf(fileInfo.getFirstCase());
    }


}
