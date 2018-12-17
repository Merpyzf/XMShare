package com.merpyzf.xmshare.ui.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.CompactFile;
import com.merpyzf.transfermanager.entity.DocFile;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.Volume;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.ui.activity.ReceivedFileActivity;
import com.merpyzf.xmshare.ui.adapter.VolumeAdapter;
import com.merpyzf.xmshare.ui.fragment.filemanager.FileManagerFragment;
import com.merpyzf.xmshare.ui.widget.tools.CustomRecyclerScrollViewListener;
import com.merpyzf.xmshare.util.StorageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class FunctionListFragment extends BaseFragment implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.tv_document_num)
    TextView mTvDocNum;
    @BindView(R.id.tv_apk_num)
    TextView mTvApkNum;
    @BindView(R.id.tv_zip_num)
    TextView mTvcompactNum;
    @BindView(R.id.ll_receive_files)
    LinearLayout mLlReceiveFiles;
    @BindView(R.id.rl_volume)
    RecyclerView mRlVolume;

    private List<DocFile> mDocFileList = new ArrayList<>();
    private List<ApkFile> mApkFileList = new ArrayList<>();
    private List<CompactFile> mCompactFileList = new ArrayList<>();
    private ArrayList<Volume> mVolumes = new ArrayList<>();
    private VolumeAdapter mVolumeAdapter;
    private CustomRecyclerScrollViewListener mScrollListener;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_function_list;
    }

    @Override
    protected void doCreateEvent() {
        int childCount = mLlReceiveFiles.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mLlReceiveFiles.getChildAt(i);
            if (view instanceof LinearLayout) {
                view.setOnClickListener(this);
            }
        }
        mVolumeAdapter.setOnItemClickListener(this);
    }

    /**
     * 加载数据
     */
    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        super.initData();
        mVolumes.addAll(StorageUtils.getVolumes(getContext()));
        mVolumeAdapter.notifyDataSetChanged();

    }

    /**
     * 对View进行相关的初始化工作
     *
     * @param rootView
     */
    @Override
    protected void doCreateView(View rootView) {
        mRlVolume.setLayoutManager(new LinearLayoutManager(mContext));
        mVolumeAdapter = new VolumeAdapter(mContext, R.layout.item_rv_volume, mVolumes);
        mRlVolume.setAdapter(mVolumeAdapter);

    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), ReceivedFileActivity.class);
        switch (v.getId()) {
            case R.id.ll_app:
                intent.putExtra("fileType", BaseFileInfo.FILE_TYPE_APP);
                startActivity(intent);
                break;
            case R.id.ll_image:
                intent.putExtra("fileType", BaseFileInfo.FILE_TYPE_IMAGE);
                startActivity(intent);
                break;
            case R.id.ll_music:
                intent.putExtra("fileType", BaseFileInfo.FILE_TYPE_MUSIC);
                startActivity(intent);
                break;
            case R.id.ll_video:
                intent.putExtra("fileType", BaseFileInfo.FILE_TYPE_VIDEO);
                startActivity(intent);
                break;
            case R.id.ll_other:
                intent.putExtra("fileType", BaseFileInfo.FILE_TYPE_STORAGE);
                startActivity(intent);
                break;

            default:
                break;
        }

    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Volume volume = (Volume) adapter.getItem(position);
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity())
                .getSupportFragmentManager().beginTransaction();
        FileManagerFragment fileManagerFragment = new FileManagerFragment();
        fileManagerFragment.setScrollListener(mScrollListener);
        String volumeName;
        if (volume.isRemovable()) {
            volumeName = "SD卡";
        } else {
            volumeName = "手机存储";
        }
        Bundle bundle = new Bundle();
        bundle.putCharSequence("rootPath", volume.getPath());
        bundle.putCharSequence("volumeName", volumeName);
        fileManagerFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_main_container, fileManagerFragment, Const.TAG_FILE_MANAGER);
        fragmentTransaction.commit();
    }

    public void setScrollListener(CustomRecyclerScrollViewListener scrollListener) {
        this.mScrollListener = scrollListener;
    }
}
