package com.merpyzf.xmshare.ui.adapter;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.util.FilePathManager;
import com.merpyzf.transfermanager.util.FormatUtils;
import com.merpyzf.transfermanager.util.Md5Utils;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.PinnedHeaderEntity;
import com.merpyzf.xmshare.common.base.BaseHeaderAdapter;

import java.io.File;
import java.util.List;

/**
 * Created by wangke on 2018/4/22.
 */

public class ReceivedFileAdapter extends BaseHeaderAdapter<PinnedHeaderEntity<FileInfo>> {


    public ReceivedFileAdapter(List<PinnedHeaderEntity<FileInfo>> data, int fileType) {
        super(data, fileType);
    }

    @Override
    protected void addItemTypes() {

        Log.i("wk", "fileType->" + fileType + " FileInfo.FILE_TYPE_IMAGE->" + FileInfo.FILE_TYPE_IMAGE);
        if (fileType == FileInfo.FILE_TYPE_IMAGE) {
            addItemType(BaseHeaderAdapter.TYPE_DATA, R.layout.item_rv_pic);

        } else {
            addItemType(BaseHeaderAdapter.TYPE_DATA, R.layout.item_rv_received_file);
        }

        addItemType(BaseHeaderAdapter.TYPE_HEADER, R.layout.item_pinned_header);

    }

    @Override
    protected void convert(BaseViewHolder helper, PinnedHeaderEntity<FileInfo> item) {
        ImageView iv = helper.getView(R.id.iv_cover);
        int errorImgRes = -1;
        int placeHolderImgRes = -1;
        String loadImgPath = null;
        TextView tvActionTip = helper.getView(R.id.tv_action_tip);
        switch (helper.getItemViewType()) {
            case com.merpyzf.xmshare.ui.test.adapter.BaseHeaderAdapter.TYPE_DATA:
                if (fileType == FileInfo.FILE_TYPE_IMAGE) {
                    errorImgRes = R.drawable.ic_holder_image;
                    placeHolderImgRes = R.drawable.ic_holder_image;
                    loadImgPath = item.getData().getPath();
                } else if (fileType == FileInfo.FILE_TYPE_APP) {
                    tvActionTip.setText("安装");
                    helper.setText(R.id.tv_name, item.getData().getName() + "");
                    float size = FormatUtils.convert2Mb(new File(item.getData().getPath()).length());
                    helper.setText(R.id.tv_size, size + "mb");
                    loadImgPath = FilePathManager.getAppThumbCacheDir().getPath() + File.separator + Md5Utils.getMd5(item.getData().getName());
                    errorImgRes = R.drawable.ic_main_app;
                    placeHolderImgRes = R.drawable.ic_main_app;
                } else if (fileType == FileInfo.FILE_TYPE_MUSIC) {
                    tvActionTip.setVisibility(View.INVISIBLE);
                    helper.setText(R.id.tv_name, item.getData().getName() + "");
                    float size = FormatUtils.convert2Mb(new File(item.getData().getPath()).length());
                    helper.setText(R.id.tv_size, size + "mb");
                    loadImgPath = FilePathManager.getMusicAlbumCacheDir().getPath() + File.separator + Md5Utils.getMd5(item.getData().getName());
                    errorImgRes = R.drawable.ic_holder_music;
                    placeHolderImgRes = R.drawable.ic_holder_music;
                } else if (fileType == FileInfo.FILE_TYPE_VIDEO) {
                    tvActionTip.setVisibility(View.INVISIBLE);
                    helper.setText(R.id.tv_name, item.getData().getName() + "");
                    float size = FormatUtils.convert2Mb(new File(item.getData().getPath()).length());
                    helper.setText(R.id.tv_size, size + "mb");
                    loadImgPath = FilePathManager.getVideoThumbCacheDir().getPath() + File.separator + Md5Utils.getMd5(item.getData().getName());
                    errorImgRes = R.drawable.ic_holder_video;
                    placeHolderImgRes = R.drawable.ic_holder_video;
                }
                Glide.with(mContext)
                        .load(loadImgPath)
                        .error(errorImgRes)
                        .placeholder(placeHolderImgRes)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(iv);
                break;
            case com.merpyzf.xmshare.ui.test.adapter.BaseHeaderAdapter.TYPE_HEADER:
                helper.setText(R.id.tv_pinner_title, item.getPinnedHeaderName());
                break;
            default:
                break;
        }


    }
}
