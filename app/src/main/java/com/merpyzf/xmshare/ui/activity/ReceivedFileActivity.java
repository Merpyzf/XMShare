package com.merpyzf.xmshare.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.util.FilePathManager;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.FileInfoFactory;
import com.merpyzf.xmshare.bean.PinnedHeaderEntity;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.common.base.BaseHeaderAdapter;
import com.merpyzf.xmshare.ui.adapter.ReceivedFileAdapter;
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration;
import com.oushangfeng.pinnedsectionitemdecoration.callback.OnHeaderClickListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 浏览已接收到的文件
 */
public class ReceivedFileActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    List<PinnedHeaderEntity<FileInfo>> mFileInfoList = new ArrayList<>();
    private ReceivedFileAdapter mAdapter;
    private int mFileType;


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_received_file;
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {

        initRecyclerView();
        Log.i("wk", "浏览文件的文件类型" + mFileType);
        mAdapter = new ReceivedFileAdapter(mFileInfoList, mFileType);
        mAdapter.setEmptyView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_rv_file_empty, null, false));
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void initToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getToolBarTitle(mFileType));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private CharSequence getToolBarTitle(int mFileType) {

        switch (mFileType) {
            case FileInfo.FILE_TYPE_APP:
                return "收到的应用";
            case FileInfo.FILE_TYPE_IMAGE:
                return "收到的图片";
            case FileInfo.FILE_TYPE_MUSIC:
                return "收到的音乐";
            case FileInfo.FILE_TYPE_VIDEO:
                return "收到的视频";
            case FileInfo.FILE_TYPE_OTHER:
                return "其他";
            default:
                break;

        }
        return null;
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();

        mRecyclerView.addItemDecoration(new PinnedHeaderItemDecoration.Builder(com.merpyzf.xmshare.ui.test.adapter.BaseHeaderAdapter.TYPE_HEADER).setDividerId(R.drawable.divider).enableDivider(true)
                .setHeaderClickListener(new OnHeaderClickListener() {
                    @Override
                    public void onHeaderClick(View view, int id, int position) {

                    }

                    @Override
                    public void onHeaderLongClick(View view, int id, int position) {

                    }

                    @Override
                    public void onHeaderDoubleClick(View view, int id, int position) {

                    }

                }).create());

        if (mFileType == FileInfo.FILE_TYPE_IMAGE) {

            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        } else {

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        }


    }

    @Override
    protected void initEvents() {

    }

    // TODO: 2018/8/7 fix: 缩短代码行数
    @Override
    protected void initData() {
        super.initData();
        // 这个方法中的数据初始化耗时操作
        // 初始化待显示的数据
        mFileType = getIntent().getIntExtra("fileType", -1);
        File receiveDir = null;
        switch (mFileType) {
            // 应用
            case FileInfo.FILE_TYPE_APP:
                receiveDir = FilePathManager.getSaveAppDir();
                break;
            case FileInfo.FILE_TYPE_IMAGE:
                receiveDir = FilePathManager.getSavePhotoDir();
                break;
            case FileInfo.FILE_TYPE_MUSIC:
                receiveDir = FilePathManager.getSaveMusicDir();
                break;
            case FileInfo.FILE_TYPE_VIDEO:
                receiveDir = FilePathManager.getSaveVideoDir();
                break;
            case FileInfo.FILE_TYPE_OTHER:
                break;
            default:
                break;


        }

        Map<String, List<FileInfo>> map = null;
        if (receiveDir != null) {

            File[] files = receiveDir.listFiles();
            map = new HashMap<>();
            for (File file : files) {
                Date modifiedDate = new Date(file.lastModified());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String strModifiedDate = simpleDateFormat.format(modifiedDate);
                List<FileInfo> fileInfoList = map.get(strModifiedDate);
                if (fileInfoList == null) {
                    fileInfoList = new ArrayList<>();
                    fileInfoList.add(FileInfoFactory.toFileInfoType(file, mFileType));
                    map.put(strModifiedDate, fileInfoList);

                } else {
                    fileInfoList.add(FileInfoFactory.toFileInfoType(file, mFileType));
                }
            }

            List<String> tempKeyList = new ArrayList();

            for (String key : map.keySet()) {
                tempKeyList.add(key.replace("-", "/"));
            }

            // 对map中的key的时间按照降序排列
            Collections.sort(tempKeyList, (o1, o2) -> {

                long t1 = new Date(o1).getTime();
                long t2 = new Date(o2).getTime();

                if (t1 > t2) {
                    return -1;
                } else {
                    return 1;
                }

            });
            for (String key : tempKeyList) {
                mFileInfoList.add(new PinnedHeaderEntity<>(null, BaseHeaderAdapter.TYPE_HEADER, key.replace("/", "-")));
                for (FileInfo fileInfo : map.get(key.replace("/", "-"))) {
                    // 可能需要按照时间顺序进行排序
                    mFileInfoList.add(new PinnedHeaderEntity<>(fileInfo, BaseHeaderAdapter.TYPE_DATA, key));
                }


            }


        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }
}
