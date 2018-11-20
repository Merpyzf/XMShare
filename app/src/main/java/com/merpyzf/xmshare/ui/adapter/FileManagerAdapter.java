package com.merpyzf.xmshare.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.FileInfo;
import com.merpyzf.xmshare.util.FileUtils;

import java.io.File;
import java.util.List;

import static com.merpyzf.transfermanager.util.FileUtils.getFileSizeArrayStr;

public class FileManagerAdapter extends BaseQuickAdapter<FileInfo, BaseViewHolder> {

    public FileManagerAdapter(int layoutResId, @Nullable List<FileInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FileInfo item) {
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
            helper.setImageResource(R.id.iv_ico, R.drawable.ic_folder);
        } else {
            fileInfo.append("文件大小: ");
            String[] fileSizeArray = getFileSizeArrayStr(item.getSize());
            fileInfo.append(fileSizeArray[0]);
            fileInfo.append(fileSizeArray[1]);



            helper.setImageResource(R.id.iv_ico, R.drawable.ic_other);
        }
        helper.setText(R.id.tv_title, item.getName());
        helper.setText(R.id.tv_info, fileInfo.toString());
    }
}
