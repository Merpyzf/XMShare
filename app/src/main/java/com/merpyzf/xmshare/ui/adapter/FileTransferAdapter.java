package com.merpyzf.xmshare.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.entity.VideoFile;
import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.transfermanager.util.Md5Utils;
import com.merpyzf.xmshare.R;

import java.io.File;
import java.util.List;

/**
 * Created by wangke on 2018/1/18.
 * 文件传输列表
 */
public class FileTransferAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

    public static final int TYPE_SEND = 1;
    public static final int TYPE_RECEIVE = 2;
    private int mType;

    public FileTransferAdapter(int layoutResId, int type, @Nullable List<T> data) {
        super(layoutResId, data);
        this.mType = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        // 缩略图
        ImageView ivThumb = helper.getView(R.id.iv_file_thumb);
        // 传输完成标记
        ImageView ivDone = helper.getView(R.id.iv_done);
        // 设置传输的文件的大小
        TextView tvSize = helper.getView(R.id.tv_size);
        // 传输进度条
        ProgressBar progressBar = helper.getView(R.id.progress);
        // 进度提示
        TextView tvProgress = helper.getView(R.id.tv_progress);
        // 文件名
        TextView tvTitle = helper.getView(R.id.tv_title);

        // 当前条目对应的文件信息
        FileInfo fileInfo = (FileInfo) item;
        String[] fileSizeArrayStr = FileUtils.getFileSizeArrayStr(fileInfo.getLength());
        tvTitle.setText(fileInfo.getName());
        tvSize.setText(fileSizeArrayStr[0] + fileSizeArrayStr[1]);
        File thumbFile = getThumbFile(fileInfo);
        Glide.with(mContext)
                .load(thumbFile)
                .placeholder(R.drawable.ic_thumb_empty)
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.ic_header)
                .into(ivThumb);
        if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_WAITING) {
            tvProgress.setText("等待中");
            progressBar.setVisibility(View.INVISIBLE);
            ivDone.setVisibility(View.INVISIBLE);
        } else if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFING) {
            ivDone.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            int currentProgress = (int) (fileInfo.getProgress() * 100);
            String[] transferSpeed = fileInfo.getTransferSpeed();
            tvProgress.setText("传输中: " + currentProgress + "%");
            progressBar.setProgress(currentProgress);
        } else if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {
            progressBar.setVisibility(View.INVISIBLE);
            ivDone.setVisibility(View.VISIBLE);
            if (mType == TYPE_RECEIVE) {
                if (fileInfo instanceof PicFile) {
                    File saveFile = FileUtils.getSaveFile(fileInfo);
                    Glide.with(mContext)
                            .load(saveFile)
                            .placeholder(R.drawable.ic_thumb_empty)
                            .crossFade()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(R.drawable.ic_header)
                            .into(ivThumb);
                }
                tvProgress.setText("传输完毕," + getOpenTypeText(fileInfo));
            } else {
                tvProgress.setText("传输完毕");
            }
        } else if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_FAILED) {
            progressBar.setProgress((int) fileInfo.getProgress());
            progressBar.setVisibility(View.VISIBLE);
            ivDone.setVisibility(View.INVISIBLE);
            tvProgress.setText("传输失败");
        }
    }
    private File getThumbFile(FileInfo fileInfo) {
        File thumbFile = null;
        if (FileTransferAdapter.TYPE_SEND == mType) {
            if (fileInfo instanceof ApkFile) {
                ApkFile apkFile = (ApkFile) fileInfo;
                thumbFile = new File(com.merpyzf.xmshare.common.Const.PIC_CACHES_DIR, Md5Utils.getMd5(apkFile.getName()));
            } else if (fileInfo instanceof MusicFile) {
                MusicFile musicFile = (MusicFile) fileInfo;
                thumbFile = new File(com.merpyzf.xmshare.common.Const.PIC_CACHES_DIR, Md5Utils.getMd5(musicFile.getAlbumId() + ""));
            } else if (fileInfo instanceof PicFile) {
                PicFile picFile = (PicFile) fileInfo;
                thumbFile = new File(picFile.getPath());
            } else if (fileInfo instanceof VideoFile) {
                VideoFile videoFile = (VideoFile) fileInfo;
                thumbFile = new File(com.merpyzf.xmshare.common.Const.PIC_CACHES_DIR, Md5Utils.getMd5(videoFile.getPath()));
            }
        } else {
            if (!(fileInfo instanceof PicFile)) {
                thumbFile = new File(com.merpyzf.xmshare.common.Const.PIC_CACHES_DIR, Md5Utils.getMd5(fileInfo.getName()));
            }
        }

        return thumbFile;
    }

    public String getOpenTypeText(FileInfo fileInfo) {

        String typeText = null;

        switch (fileInfo.getType()) {
            case FileInfo.FILE_TYPE_APP:
                typeText = "点击安装";
                break;
            case FileInfo.FILE_TYPE_MUSIC:
            case FileInfo.FILE_TYPE_VIDEO:
                typeText = "点击播放";
                break;
            case FileInfo.FILE_TYPE_IMAGE:
                typeText = "点击查看";
                break;
            default:
                break;
        }
        return typeText;

    }
}