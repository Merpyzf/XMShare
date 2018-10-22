package com.merpyzf.xmshare.common.base;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.observer.TransferObserver;
import com.merpyzf.transfermanager.receive.ReceiverManager;
import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.transfermanager.util.Md5Utils;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;

import java.io.File;

public class MyBaseViewHolder extends BaseViewHolder implements TransferObserver {
    private String takeFileName="";
    private final ImageView ivThumb;
    private final ImageView ivDone;
    private final TextView tvSize;
    private final ProgressBar progressBar;
    private final TextView tvProgress;
    private final TextView tvTitle;
    private Context mContext;

    public MyBaseViewHolder(View view) {
        super(view);
        mContext = view.getContext();

        // 缩略图
        ivThumb = getView(R.id.iv_file_thumb);
        // 传输完成标记
        ivDone = getView(R.id.iv_done);
        // 设置传输的文件的大小
        tvSize = getView(R.id.tv_size);
        // 传输进度条
        progressBar = getView(R.id.progress);
        // 进度提示
        tvProgress = getView(R.id.tv_progress);
        // 文件名
        tvTitle = getView(R.id.tv_title);
        ReceiverManager receiverManager = ReceiverManager.getInstance(view.getContext());
        receiverManager.register(this);
    }

    public void setTransferInfo(String fileName){
        this.takeFileName = fileName;
        Log.i("WW2K", hashCode()+"===> "+fileName);
    }


    @Override
    public void onTransferProgress(FileInfo fileInfo) {

        if(takeFileName == fileInfo.getName()){

            // 传输中
            if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFING) {


                // 如果可见设置为不可见
                if (ivDone.getVisibility() == View.VISIBLE) {
                    ivDone.setVisibility(View.INVISIBLE);
                }

                // 如果进度条不可见则设置为可见
                if (progressBar.getVisibility() == View.INVISIBLE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                int currentProgress = (int) (fileInfo.getProgress() * 100);
                progressBar.setProgress(currentProgress);
                tvProgress.setText("传输进度:" + currentProgress + " %");


            }
            // 等待传输中
            else if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_WAITING) {

                tvProgress.setText("等待中");
                progressBar.setVisibility(View.INVISIBLE);
                ivDone.setVisibility(View.INVISIBLE);


            } else if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {

                progressBar.setProgress(100);
                progressBar.setVisibility(View.INVISIBLE);
                ivDone.setVisibility(View.VISIBLE);
                tvProgress.setText("传输完毕,点击播放文件");


            } else if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_FAILED) {

                progressBar.setProgress(100);
                progressBar.setVisibility(View.INVISIBLE);
                ivDone.setVisibility(View.VISIBLE);
                tvProgress.setText("传输失败");


            }
        }else {
            return;
        }


    }

    @Override
    public void onTransferStatus(FileInfo fileInfo) {

        if (fileInfo.getName().equals(fileInfo.getName())) {

            if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {

                tvProgress.setText("传输完毕,点击播放文件");
                progressBar.setProgress(100);
                progressBar.setVisibility(View.INVISIBLE);
                ivDone.setVisibility(View.VISIBLE);

                // 文件全部传输成功之后重置待传输文件的状态
                if (fileInfo.getIsLast() == Const.IS_LAST) {
                    App.resetSelectedFilesStatus();
                }

                ReceiverManager.getInstance(mContext).unRegister(this);

            } else if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_FAILED) {

                tvProgress.setText("传输失败");
            } else if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_WAITING) {


                tvProgress.setText("等待中");
                progressBar.setVisibility(View.INVISIBLE);
                ivDone.setVisibility(View.INVISIBLE);
            }

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
            } else {

                // 接受到的文件缩略图的名字为待接收的文件的MD5的值
                File thumbFile = new File(com.merpyzf.xmshare.common.Const.PIC_CACHES_DIR,
                        Md5Utils.getMd5(fileInfo.getName()));

                Glide.with(mContext)
                        .load(thumbFile)
                        .placeholder(R.drawable.ic_thumb_empty)
                        .crossFade()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .error(R.drawable.ic_header)
                        .into(ivThumb);

            }

        }else {
            return;
        }




    }

    @Override
    public void onTransferError(String error) {

    }
}
