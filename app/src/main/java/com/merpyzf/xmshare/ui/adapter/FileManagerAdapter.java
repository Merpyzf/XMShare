package com.merpyzf.xmshare.ui.adapter;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.FileInfo;
import com.merpyzf.xmshare.util.FileTypeHelper;
import com.merpyzf.xmshare.util.FileUtils;
import com.merpyzf.xmshare.util.UiUtils;

import java.io.File;
import java.util.List;

import static com.merpyzf.transfermanager.util.FileUtils.getFileSizeArrayStr;

public class FileManagerAdapter extends BaseQuickAdapter<FileInfo, BaseViewHolder> {

    public FileManagerAdapter(int layoutResId, @Nullable List<FileInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FileInfo item) {
        ImageView ivIco = helper.getView(R.id.iv_ico);
        ImageView ivSelect = helper.getView(R.id.iv_select);
        helper.addOnClickListener(R.id.iv_select);
        StringBuffer fileInfo = new StringBuffer();
        int folderCount = 0;
        int fileCount = 0;
        if (item.isDirectory()) {
            File file = new File(item.getPath());
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    ++fileCount;
                }
                if (f.isDirectory()) {
                    ++folderCount;
                }
            }
            fileInfo.append("文件: ");
            fileInfo.append(fileCount);
            fileInfo.append(", 文件夹: ");
            fileInfo.append(folderCount);
            ivIco.setImageResource(R.drawable.ic_folder);
        } else {
            fileInfo.append("文件大小: ");
            String[] fileSizeArray = getFileSizeArrayStr(item.getSize());
            fileInfo.append(fileSizeArray[0]);
            fileInfo.append(fileSizeArray[1]);
            if (item.isPhoto()) {
                Glide.with(mContext)
                        .load(item.getPath())
                        .error(R.drawable.ic_holder_image)
                        .placeholder(R.drawable.ic_holder_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(ivIco);
            } else {
                ivIco.setImageResource(FileTypeHelper.getIcoResBySuffix(item.getSuffix()));
            }
        }
        helper.setText(R.id.tv_title, item.getName());
        helper.setText(R.id.tv_info, fileInfo.toString());

        if (App.isContain(item)) {
            ivSelect.setImageResource(R.drawable.ic_cb_checked);
            Log.i("WW2k", item.getPath()+" isContain: true");
        } else {
            ivSelect.setImageResource(R.drawable.ic_cb_unchecked);
            Log.i("WW2k", item.getPath()+" isContain: false");
        }
    }
}
