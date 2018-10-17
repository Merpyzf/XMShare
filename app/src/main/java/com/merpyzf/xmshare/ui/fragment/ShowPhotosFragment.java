package com.merpyzf.xmshare.ui.fragment;


import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.PhotoDirBean;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.receiver.FileSelectedListChangedReceiver;
import com.merpyzf.xmshare.ui.adapter.FileAdapter;
import com.merpyzf.xmshare.ui.activity.SelectFilesActivity;
import com.merpyzf.xmshare.util.AnimationUtils;
import com.merpyzf.xmshare.util.Md5Utils;

import java.util.Objects;

import butterknife.BindView;

/**
 * 展示相册中图片的Fragment
 *
 * @author wangke
 */

public class ShowPhotosFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.rv_photo_list)
    RecyclerView mRvPhotoList;

    private View mBottomSheetView;
    private PhotoFragment mPhotoFrg;
    private FileAdapter<FileInfo> mAdapter;
    private FileSelectedListChangedReceiver mFslcReceiver;
    private CheckBox mCheckBoxAll;
    private PhotoDirBean mPhotoDirBean;
    private static final String TAG = ShowPhotosFragment.class.getSimpleName();

    public static ShowPhotosFragment getInstance(Bundle args) {
        ShowPhotosFragment showPhotosFragment = new ShowPhotosFragment();
        showPhotosFragment.setArguments(args);
        return showPhotosFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoDirBean = (PhotoDirBean) Objects.requireNonNull(getArguments()).getSerializable("photos");
        Log.i("wk", "onCreate执行了, 查看的目录-->" + mPhotoDirBean.getName());
    }


    @Override
    public void onResume() {
        super.onResume();
        registerFileListChangedReceiver();
    }


    @Override
    protected void initWidget(View rootView) {
        mPhotoFrg = getMyParentFragment();
        mBottomSheetView = Objects.requireNonNull(getActivity()).findViewById(R.id.bottom_sheet);
        mRvPhotoList.setLayoutManager(new GridLayoutManager(mContext, 4));
        mAdapter = new FileAdapter<>(getActivity(), R.layout.item_rv_pic, mPhotoDirBean.getImageList());
        mRvPhotoList.setAdapter(mAdapter);
        mCheckBoxAll = getCheckBoxFromParentFrg();
        mCheckBoxAll.setChecked(mPhotoDirBean.isChecked());
        if (null != mPhotoFrg) {
            mPhotoFrg.getTvTitle().setText("图片(" + mPhotoDirBean.getImageList().size() + ")");
        }
        if (mPhotoDirBean.isChecked()) {
            mPhotoFrg.getTvChecked().setText("取消全选");
        } else {
            mPhotoFrg.getTvChecked().setText("全选");
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_test;
    }

    @Override
    protected void initData() {
        super.initData();

    }
    @Override
    protected void initEvent() {
        mAdapter.setOnItemClickListener(this);
        mCheckBoxAll.setOnCheckedChangeListener(this);
        mFslcReceiver = new FileSelectedListChangedReceiver() {
            @Override
            public void onFileListChanged(String unSelectedFile) {
                mAdapter.notifyDataSetChanged();
                mCheckBoxAll.setChecked(isCheckedAllPhotos());
            }
        };
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ImageView ivSelect = view.findViewById(R.id.iv_select);
        FileInfo fileInfo = mPhotoDirBean.getImageList().get(position);
        if (!App.getSendFileList().contains(fileInfo)) {
            ivSelect.setVisibility(View.VISIBLE);
            // 添加选中的文件
            App.addSendFile(fileInfo);
            fileInfo.setMd5(Md5Utils.getFileMd5(fileInfo));
            if (mPhotoFrg != null) {
                mPhotoFrg.selectPhoto((PicFile) fileInfo);
            }
            View startView;
            View targetView = null;
            startView = view.findViewById(R.id.iv_cover);
            AnimationUtils.zoomOutCover(startView, 200);
            if (getActivity() != null && (getActivity() instanceof SelectFilesActivity)) {
                targetView = mBottomSheetView;
            }
            AnimationUtils.setAddTaskAnimation(getActivity(), startView, targetView, null);
            if (isCheckedAllPhotos()) {
                mPhotoFrg.getCheckbox().setChecked(true);
                mPhotoDirBean.setChecked(true);
            } else {
                mPhotoFrg.getCheckbox().setChecked(false);
                mPhotoDirBean.setChecked(false);
            }
        } else {
            AnimationUtils.zoomInCover(view.findViewById(R.id.iv_cover), 200);
            ivSelect.setVisibility(View.INVISIBLE);
            App.removeSendFile(fileInfo);
            // 将移除文件的事件通知外部
            if (mPhotoFrg != null) {
                mPhotoFrg.unSelectPhoto((PicFile) fileInfo);
            }
            if (isCheckedAllPhotos()) {
                mPhotoFrg.getCheckbox().setChecked(true);
                mPhotoDirBean.setChecked(true);
            } else {
                mPhotoFrg.getCheckbox().setChecked(false);
                mPhotoDirBean.setChecked(false);
            }
        }


    }

    private boolean isCheckedAllPhotos() {
        for (FileInfo fileInfo : mPhotoDirBean.getImageList()) {
            if (!App.getSendFileList().contains(fileInfo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 注册选择的文件列表发生改变的广播
     */
    private void registerFileListChangedReceiver() {
        // 动态注册监听选择文件列表发生改变的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileSelectedListChangedReceiver.ACTION);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mFslcReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mFslcReceiver);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getTag().equals(TAG)) {
            if (isChecked) {
                mPhotoDirBean.setChecked(true);
                mPhotoFrg.checkAllPhoto(mPhotoDirBean.getImageList());
                mPhotoFrg.getTvChecked().setText("取消全选");
            } else {
                if (isCheckedAllPhotos()) {
                    mPhotoDirBean.setChecked(false);
                    mPhotoFrg.cancelCheckAllPhoto(mPhotoDirBean.getImageList());
                }
                mPhotoFrg.getTvChecked().setText("全选");
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        mFslcReceiver = null;
        Log.i("wk", "onDestroy执行了");
        super.onDestroy();
    }

    public PhotoFragment getMyParentFragment() {
        PhotoFragment photoFrg = null;
        for (Fragment fragment : Objects.requireNonNull(getActivity()).getSupportFragmentManager().getFragments()) {
            if (fragment instanceof PhotoFragment) {
                photoFrg = (PhotoFragment) fragment;
            }
        }
        return photoFrg;
    }

    public CheckBox getCheckBoxFromParentFrg() {
        CheckBox checkBox = null;
        if (null != mPhotoFrg) {
            checkBox = mPhotoFrg.getCheckbox();
            checkBox.setTag(TAG);
        }
        return checkBox;
    }
}
