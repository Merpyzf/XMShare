package com.merpyzf.xmshare.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.entity.StorageFile;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.util.FileTypeHelper;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

import static com.merpyzf.transfermanager.util.FileUtils.getFileSizeArrayStr;

/**
 * 文件管理列表的适配器
 * @author wangke
 */
public class FileManagerAdapter extends BaseQuickAdapter<StorageFile, BaseViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    public FileManagerAdapter(int layoutResId, @Nullable List<StorageFile> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StorageFile item) {
        ImageView ivIco = helper.getView(R.id.iv_ico);
        ImageView ivSelect = helper.getView(R.id.iv_select);
        helper.addOnClickListener(R.id.iv_select);
        StringBuffer fileInfo = new StringBuffer();
        if (item.isDirectory()) {
            fileInfo.append("文件: ");
            fileInfo.append(item.getFileNum());
            fileInfo.append(", 文件夹: ");
            fileInfo.append(item.getFolderNum());
            ivIco.setImageResource(R.drawable.ic_fileitem_folder);
        } else {
            fileInfo.append("文件大小: ");
            String[] fileSizeArray = getFileSizeArrayStr(item.getLength());
            fileInfo.append(fileSizeArray[0]);
            fileInfo.append(fileSizeArray[1]);
            if (item.isPhoto()) {
                Glide.with(mContext)
                        .load(item.getPath())
                        .error(R.drawable.ic_holder_image)
                        .placeholder(R.drawable.ic_holder_image)
                        .crossFade()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(ivIco);
            } else {
                ivIco.setImageResource(FileTypeHelper.getIcoResBySuffix(item.getSuffix()));
            }
        }
        helper.setText(R.id.tv_title, item.getName());
        helper.setText(R.id.tv_info, fileInfo.toString());
        if (!item.isDirectory()) {
            if (App.isContain(item)) {
                ivSelect.setImageResource(R.drawable.ic_cb_checked);
            } else {
                ivSelect.setImageResource(R.drawable.ic_cb_unchecked);
            }
        } else {
            ivSelect.setImageResource(R.drawable.ic_arrow_right_p);
        }
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(mData.get(position).getFirstLetter());
    }
}
