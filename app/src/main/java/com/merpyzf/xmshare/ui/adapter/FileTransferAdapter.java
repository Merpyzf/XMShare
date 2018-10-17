package com.merpyzf.xmshare.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.merpyzf.transfermanager.interfaces.TransferObserver;
import com.merpyzf.transfermanager.receive.ReceiverManager;
import com.merpyzf.transfermanager.send.SenderManager;
import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.transfermanager.util.Md5Utils;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.util.List;

/**
 * Created by wangke on 2018/1/18.
 * 文件传输列表
 */

// TODO: 2018/4/26 这个Adapter的代码太乱了，需要花时间规整
public class FileTransferAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private List<FileInfo> mFileLists;
    public static final int TYPE_SEND = 1;
    public static final int TYPE_RECEIVE = 2;
    private int type = -1;


    public FileTransferAdapter(int layoutResId, int type, @Nullable List<T> data) {
        super(layoutResId, data);
        this.type = type;
        this.mFileLists = (List<FileInfo>) data;
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        // 当前条目对应的文件信息
        FileInfo file = (FileInfo) item;
        String fileName = file.getName();

        // 缩略图
        ImageView ivThumb = helper.getView(R.id.iv_file_thumb);
        // 传输完成标记
        ImageView ivDone = helper.getView(R.id.iv_done);
        // 设置传输的文件的大小
        TextView tvSize = helper.getView(R.id.tv_size);
        int length = file.getLength();
        String[] fileSizeArrayStr = FileUtils.getFileSizeArrayStr(length);
        tvSize.setText(fileSizeArrayStr[0] + fileSizeArrayStr[1]);
        // 传输进度条
        ProgressBar progressBar = helper.getView(R.id.progress);
        // 进度提示
        TextView tvProgress = helper.getView(R.id.tv_progress);

        // 文件名
        TextView tvTitle = helper.getView(R.id.tv_title);
        tvTitle.setText(file.getName());


        File thumbFile = null;
        // 缩略图所在的路径
        String thumbPath = null;
        // 加载图片
        if (FileTransferAdapter.TYPE_SEND == type) {

            if (item instanceof ApkFile) {
                ApkFile apkFile = (ApkFile) item;
                thumbPath = com.merpyzf.xmshare.common.Const.PIC_CACHES_DIR + File.separator + Md5Utils.getMd5(apkFile.getName());

            } else if (item instanceof MusicFile) {

                MusicFile musicFile = (MusicFile) item;
                thumbPath = com.merpyzf.xmshare.common.Const.PIC_CACHES_DIR + File.separator + Md5Utils.getMd5(musicFile.getAlbumId() + "");


            } else if (item instanceof PicFile) {

                PicFile picFile = (PicFile) item;
                thumbPath = picFile.getPath();


            } else if (item instanceof VideoFile) {

                VideoFile videoFile = (VideoFile) item;
                thumbPath = com.merpyzf.xmshare.common.Const.PIC_CACHES_DIR + File.separator + Md5Utils.getMd5(videoFile.getPath());


            }

            Glide.with(mContext)
                    .load(thumbPath)
                    .placeholder(R.drawable.ic_holder_video)
                    .error(R.drawable.ic_holder_video)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(ivThumb);


        } else if (FileTransferAdapter.TYPE_RECEIVE == type) {

            Log.i("wk", "ssss-> name --> " + file.getName());
            // 接受到的文件缩略图的名字为待接收的文件的MD5的值
            thumbFile = new File(com.merpyzf.xmshare.common.Const.PIC_CACHES_DIR, Md5Utils.getMd5(file.getName()));

            Glide.with(mContext)
                    .load(thumbFile)
                    .placeholder(R.drawable.ic_thumb_empty)
                    .crossFade()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(R.drawable.ic_header)
                    .into(ivThumb);


        }


        // 等待传输中
        if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_WAITING) {


            tvProgress.setText("等待中");
            progressBar.setVisibility(View.INVISIBLE);
            ivDone.setVisibility(View.INVISIBLE);


        } else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFING) {

            ivDone.setVisibility(View.INVISIBLE);
            // 如果进度条不可见则设置为可见
            progressBar.setVisibility(View.VISIBLE);
            int currentProgress = (int) (file.getProgress() * 100);
            progressBar.setProgress(currentProgress);


        } else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {

            progressBar.setVisibility(View.INVISIBLE);
            ivDone.setVisibility(View.VISIBLE);

            if (type == TYPE_RECEIVE) {
                tvProgress.setText("传输完毕," + getOpenTypeText(file));

                if (file instanceof PicFile) {

                    File saveFile = FileUtils.getSaveFile(file);
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
                    thumbFile = new File(com.merpyzf.xmshare.common.Const.PIC_CACHES_DIR, Md5Utils.getMd5(file.getName()));

                    Glide.with(mContext)
                            .load(thumbFile)
                            .placeholder(R.drawable.ic_thumb_empty)
                            .crossFade()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(R.drawable.ic_header)
                            .into(ivThumb);

                }

            } else {
                tvProgress.setText("传输完毕");
            }


        } else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_FAILED) {

            progressBar.setProgress(100);
            progressBar.setVisibility(View.INVISIBLE);
            ivDone.setVisibility(View.VISIBLE);
            tvProgress.setText("传输失败");
        }


        if (type == TYPE_RECEIVE) {

            ReceiverManager.getInstance(mContext).register(new TransferObserver() {
                @Override
                public void onTransferProgress(FileInfo fileInfo) {


                    if (file.getName().equals(fileInfo.getName())) {


                        // 传输中
                        if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFING) {


                            // 如果可见设置为不可见
                            if (ivDone.getVisibility() == View.VISIBLE) {
                                ivDone.setVisibility(View.INVISIBLE);
                            }

                            // 如果进度条不可见则设置为可见
                            if (progressBar.getVisibility() == View.INVISIBLE) {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                            int currentProgress = (int) (file.getProgress() * 100);
                            progressBar.setProgress(currentProgress);
                            tvProgress.setText("传输进度:" + currentProgress + " %");


                        }
                        // 等待传输中
                        else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_WAITING) {

                            tvProgress.setText("等待中");
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.INVISIBLE);


                        } else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {

                            progressBar.setProgress(100);
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.VISIBLE);
                            tvProgress.setText("传输完毕," + getOpenTypeText(fileInfo));


                        } else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_FAILED) {

                            progressBar.setProgress(100);
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.VISIBLE);
                            tvProgress.setText("传输失败");


                        }


                    } else {


                        // 等待传输中
                        if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_WAITING) {

                            tvProgress.setText("等待中");
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.INVISIBLE);


                        } else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {

                            progressBar.setProgress(100);
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.VISIBLE);
                            tvProgress.setText("传输完毕," + getOpenTypeText(fileInfo));


                        } else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_FAILED) {

                            progressBar.setProgress(100);
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.VISIBLE);
                            tvProgress.setText("传输失败");


                        }

                    }

                }

                @Override
                public void onTransferStatus(FileInfo fileInfo) {

                    if (fileInfo.getName().equals(file.getName())) {

                        if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {

                            tvProgress.setText("传输完毕," + getOpenTypeText(fileInfo));
                            progressBar.setProgress(100);
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.VISIBLE);

                            // 文件全部传输成功之后重置待传输文件的状态
                            if (fileInfo.getIsLast() == Const.IS_LAST) {
                                App.resetSelectedFilesStatus();
                            }

                            Log.i(TAG, "传输完毕TRANSFER_SUCCESS");
                            ReceiverManager.getInstance(mContext).unRegister(this);

                        } else if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_FAILED) {

                            tvProgress.setText("传输失败");
                        } else if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_WAITING) {


                            tvProgress.setText("等待中");
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.INVISIBLE);
                        }

                        if (file instanceof PicFile) {

                            File saveFile = FileUtils.getSaveFile(file);
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
                            File thumbFile = new File(com.merpyzf.xmshare.common.Const.PIC_CACHES_DIR, Md5Utils.getMd5(file.getName()));

                            Glide.with(mContext)
                                    .load(thumbFile)
                                    .placeholder(R.drawable.ic_thumb_empty)
                                    .crossFade()
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .error(R.drawable.ic_header)
                                    .into(ivThumb);

                        }

                    }
                }

                @Override
                public void onTransferError(String error) {

                }


            });
        } else if (type == TYPE_SEND) {

            SenderManager.getInstance(mContext).register(new TransferObserver() {
                @Override
                public void onTransferProgress(FileInfo fileInfo) {


                    if (file.getName().equals(fileInfo.getName())) {


                        // 传输中
                        if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFING) {


                            // 如果可见设置为不可见
                            if (ivDone.getVisibility() == View.VISIBLE) {
                                ivDone.setVisibility(View.INVISIBLE);
                            }

                            // 如果进度条不可见则设置为可见
                            if (progressBar.getVisibility() == View.INVISIBLE) {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                            int currentProgress = (int) (file.getProgress() * 100);
                            progressBar.setProgress(currentProgress);
                            tvProgress.setText("传输进度:" + currentProgress + " %");


                        }
                        // 等待传输中
                        else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_WAITING) {

                            tvProgress.setText("等待中");
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.INVISIBLE);


                        } else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {

                            progressBar.setProgress(100);
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.VISIBLE);
                            tvProgress.setText("传输完毕");

                        } else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_FAILED) {

                            progressBar.setProgress(100);
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.VISIBLE);
                            tvProgress.setText("传输失败");


                        }


                    } else {


                        // 等待传输中
                        if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_WAITING) {

                            tvProgress.setText("等待中");
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.INVISIBLE);


                        } else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {

                            progressBar.setProgress(100);
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.VISIBLE);
                            tvProgress.setText("传输完毕");

                        } else if (file.getFileTransferStatus() == Const.TransferStatus.TRANSFER_FAILED) {

                            progressBar.setProgress(100);
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.VISIBLE);
                            tvProgress.setText("传输失败");


                        }

                    }

                }

                @Override
                public void onTransferStatus(FileInfo fileInfo) {

                    if (fileInfo.getName().equals(file.getName())) {

                        if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {

                            tvProgress.setText("传输完毕");
                            progressBar.setProgress(100);
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.VISIBLE);
                            SenderManager.getInstance(mContext).unRegister(this);
                        } else if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_FAILED) {

                            tvProgress.setText("传输失败");
                        } else if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_WAITING) {


                            tvProgress.setText("等待中");
                            progressBar.setVisibility(View.INVISIBLE);
                            ivDone.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void onTransferError(String error) {

                }

            });
        }


    }


    @NonNull
    @Override
    public String getSectionName(int position) {
        return mFileLists.get(position).getName().substring(0, 1);
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