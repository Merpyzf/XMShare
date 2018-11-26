package com.merpyzf.xmshare.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.util.FilePathManager;
import com.merpyzf.transfermanager.util.FormatUtils;
import com.merpyzf.transfermanager.util.Md5Utils;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.PinnedHeaderEntity;
import com.merpyzf.xmshare.common.base.BaseHeaderAdapter;
import com.merpyzf.xmshare.util.UiUtils;

import java.io.File;
import java.util.List;

/**
 *
 * @author wangke
 * @date 2018/4/22
 */

public class ReceivedFileAdapter extends BaseHeaderAdapter<PinnedHeaderEntity<BaseFileInfo>> {

    public ReceivedFileAdapter(List<PinnedHeaderEntity<BaseFileInfo>> data, int fileType) {
        super(data, fileType);
    }

    @Override
    protected void addItemTypes() {
        if (fileType == BaseFileInfo.FILE_TYPE_IMAGE) {
            addItemType(BaseHeaderAdapter.TYPE_DATA, R.layout.item_rv_pic);
        } else {
            addItemType(BaseHeaderAdapter.TYPE_DATA, R.layout.item_rv_received_file);
        }
        addItemType(BaseHeaderAdapter.TYPE_HEADER, R.layout.item_pinned_header);
    }

    @Override
    protected void convert(BaseViewHolder helper, PinnedHeaderEntity<BaseFileInfo> item) {
        ImageView iv = helper.getView(R.id.iv_cover);
        int errorImgRes = -1;
        int placeHolderImgRes = -1;
        String loadImgPath = null;
        TextView tvActionTip = helper.getView(R.id.tv_action_tip);
        switch (helper.getItemViewType()) {
            case BaseHeaderAdapter.TYPE_DATA:
                if (fileType == BaseFileInfo.FILE_TYPE_IMAGE) {
                    errorImgRes = UiUtils.getPlaceHolder(fileType);
                    placeHolderImgRes = UiUtils.getPlaceHolder(fileType);
                    loadImgPath = item.getData().getPath();
                } else if (fileType == BaseFileInfo.FILE_TYPE_APP) {
                    tvActionTip.setText("安装");
                    helper.setText(R.id.tv_name, item.getData().getName() + "");
                    float size = FormatUtils.convert2Mb(new File(item.getData().getPath()).length());
                    helper.setText(R.id.tv_size, size + "mb");
                    loadImgPath = FilePathManager.getLocalAppThumbCacheDir().getPath() + File.separator + Md5Utils.getMd5(item.getData().getName());
                    errorImgRes = R.drawable.ic_main_app;
                    placeHolderImgRes = R.drawable.ic_main_app;
                } else if (fileType == BaseFileInfo.FILE_TYPE_MUSIC) {
                    tvActionTip.setVisibility(View.INVISIBLE);
                    helper.setText(R.id.tv_name, item.getData().getName() + "");
                    float size = FormatUtils.convert2Mb(new File(item.getData().getPath()).length());
                    helper.setText(R.id.tv_size, size + "mb");
                    loadImgPath = FilePathManager.getLocalMusicAlbumCacheDir().getPath() + File.separator + Md5Utils.getMd5(item.getData().getName());
                    errorImgRes = UiUtils.getPlaceHolder(fileType);
                    placeHolderImgRes = UiUtils.getPlaceHolder(fileType);
                } else if (fileType == BaseFileInfo.FILE_TYPE_VIDEO) {
                    tvActionTip.setVisibility(View.INVISIBLE);
                    helper.setText(R.id.tv_name, item.getData().getName() + "");
                    float size = FormatUtils.convert2Mb(new File(item.getData().getPath()).length());
                    helper.setText(R.id.tv_size, size + "mb");
                    loadImgPath = FilePathManager.getLocalVideoThumbCacheDir().getPath() + File.separator + Md5Utils.getMd5(item.getData().getName());
                    errorImgRes = UiUtils.getPlaceHolder(fileType);
                    placeHolderImgRes = UiUtils.getPlaceHolder(fileType);
                }
                Glide.with(mContext)
                        .load(loadImgPath)
                        .error(errorImgRes)
                        .placeholder(placeHolderImgRes)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(iv);
                break;
            case BaseHeaderAdapter.TYPE_HEADER:
                helper.setText(R.id.tv_pinner_title, item.getPinnedHeaderName());
                break;
            default:
                break;
        }
    }
}
