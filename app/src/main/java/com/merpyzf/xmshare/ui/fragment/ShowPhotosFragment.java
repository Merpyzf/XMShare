package com.merpyzf.xmshare.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.PhotoDirBean;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.observer.AbsFileStatusObserver;
import com.merpyzf.xmshare.observer.FilesStatusObservable;
import com.merpyzf.xmshare.ui.adapter.FileAdapter;
import com.merpyzf.xmshare.ui.activity.SelectFilesActivity;
import com.merpyzf.xmshare.util.AnimationUtils;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * 展示相册中的照片
 * @author wangke
 */

public class ShowPhotosFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.rv_photo_list)
    RecyclerView mRvPhotoList;
    private View mBottomSheetView;
    private PhotoFragment mPhotoFrg;
    private FileAdapter<FileInfo> mAdapter;
    private CheckBox mCheckBoxAll;
    private PhotoDirBean mPhotoDirBean;
    private AbsFileStatusObserver mFileStatusObserver;
    private final String TAG = ShowPhotosFragment.class.getSimpleName();

    public static ShowPhotosFragment getInstance(Bundle args) {
        ShowPhotosFragment showPhotosFragment = new ShowPhotosFragment();
        showPhotosFragment.setArguments(args);
        return showPhotosFragment;
    }

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mPhotoDirBean = (PhotoDirBean) bundle.getSerializable("photos");
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_test;
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
    protected void initEvent() {
        mAdapter.setOnItemClickListener(this);
        mCheckBoxAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.getTag().equals(TAG)) {
                if (isChecked) {
                    mPhotoDirBean.setChecked(true);
                    FilesStatusObservable.getInstance()
                            .notifyObservers(mPhotoDirBean.getImageList(), TAG,
                                    FilesStatusObservable.FILE_SELECTED_ALL);
                    mPhotoFrg.getTvChecked().setText("取消全选");
                } else {
                    if (isSelectedAllPhotos()) {
                        mPhotoDirBean.setChecked(false);
                        FilesStatusObservable.getInstance()
                                .notifyObservers(mPhotoDirBean.getImageList(), TAG,
                                        FilesStatusObservable.FILE_CANCEL_SELECTED_ALL);
                    }
                    mPhotoFrg.getTvChecked().setText("全选");
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        mFileStatusObserver = new AbsFileStatusObserver() {
            @Override
            public void onCancelSelected(FileInfo fileInfo) {
                mAdapter.notifyDataSetChanged();
                mCheckBoxAll.setChecked(isSelectedAllPhotos());
            }

            @Override
            public void onCancelSelectedAll(List<FileInfo> fileInfoList) {
                mAdapter.notifyDataSetChanged();
                mCheckBoxAll.setChecked(isSelectedAllPhotos());
            }
        };
        FilesStatusObservable.getInstance().register(TAG, mFileStatusObserver);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ImageView ivSelect = view.findViewById(R.id.iv_select);
        FileInfo fileInfo = mPhotoDirBean.getImageList().get(position);
        if (!App.getSendFileList().contains(fileInfo)) {
            ivSelect.setVisibility(View.VISIBLE);
            App.addSendFile(fileInfo);
            // TODO: 2018/10/20 选择文件的时候是否需要从数据库中读取文件MD5值并设置进去？
            FilesStatusObservable.getInstance().notifyObservers((PicFile) fileInfo,
                    TAG, FilesStatusObservable.FILE_SELECTED);
            startFileSelectedAnimation(view);
            updatePhotoAlbumCheckedStatus();
        } else {
            AnimationUtils.zoomInCover(view.findViewById(R.id.iv_cover), 200);
            ivSelect.setVisibility(View.INVISIBLE);
            App.removeSendFile(fileInfo);
            FilesStatusObservable.getInstance().notifyObservers((PicFile) fileInfo, TAG,
                    FilesStatusObservable.FILE_CANCEL_SELECTED);
            updatePhotoAlbumCheckedStatus();
        }
    }

    /**
     * 根据所选照片的变化更新相册是否全选的状态
     */
    private void updatePhotoAlbumCheckedStatus() {
        if (isSelectedAllPhotos()) {
            mPhotoFrg.getCheckbox().setChecked(true);
            mPhotoDirBean.setChecked(true);
        } else {
            mPhotoFrg.getCheckbox().setChecked(false);
            mPhotoDirBean.setChecked(false);
        }
    }

    private void startFileSelectedAnimation(View view) {
        View startView;
        View targetView = null;
        startView = view.findViewById(R.id.iv_cover);
        AnimationUtils.zoomOutCover(startView, 200);
        if (getActivity() != null && (getActivity() instanceof SelectFilesActivity)) {
            targetView = mBottomSheetView;
        }
        AnimationUtils.setAddTaskAnimation(getActivity(), startView, targetView, null);

    }

    /**
     * 检查相册中的所有照片是否被全部选择
     *
     * @return
     */
    private boolean isSelectedAllPhotos() {
        for (FileInfo fileInfo : mPhotoDirBean.getImageList()) {
            if (!App.getSendFileList().contains(fileInfo)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        FilesStatusObservable.getInstance().remove(mFileStatusObserver);
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
