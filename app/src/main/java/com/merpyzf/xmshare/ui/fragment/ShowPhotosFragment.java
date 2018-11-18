package com.merpyzf.xmshare.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.PhotoDirBean;
import com.merpyzf.xmshare.bean.Section;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.observer.AbsFileStatusObserver;
import com.merpyzf.xmshare.observer.FilesStatusObservable;
import com.merpyzf.xmshare.ui.activity.SelectFilesActivity;
import com.merpyzf.xmshare.ui.adapter.PhotoSectionAdapter;
import com.merpyzf.xmshare.ui.widget.RecyclerViewItemDecoration;
import com.merpyzf.xmshare.util.AnimationUtils;
import com.merpyzf.xmshare.util.DateUtils;
import com.merpyzf.xmshare.util.DisplayUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;

/**
 * 展示相册中的照片
 *
 * @author wangke
 */

public class ShowPhotosFragment extends BaseFragment {

    @BindView(R.id.rv_photo_list)
    RecyclerView mRvPhotoList;
    private View mBottomSheetView;
    private PhotoFragment mPhotoFrg;
    private PhotoSectionAdapter mAdapter;
    private CheckBox mCheckBoxAll;
    private PhotoDirBean mPhotoDirBean;
    private AbsFileStatusObserver mFileStatusObserver;
    private List<Section> mDatas;
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
        mDatas = getSelectionData(mPhotoDirBean);
        updateSelectionHead();
    }

    // TODO: 2018/10/24 此方法中的业务逻辑后期使用RxJava改写
    private List<Section> getSelectionData(PhotoDirBean dirBean) {

        LinkedHashMap<String, List<FileInfo>> tempMap = new LinkedHashMap<>();
        List<Section> datas = new ArrayList<>();
        for (FileInfo fileInfo : dirBean.getImageList()) {
            File file = new File(fileInfo.getPath());
            long lastModified = file.lastModified();
            String date = DateUtils.getDate(lastModified);
            if (tempMap.keySet().contains(date)) {
                tempMap.get(date).add(fileInfo);
            } else {
                ArrayList<FileInfo> fileInfos = new ArrayList<>();
                fileInfos.add(fileInfo);
                tempMap.put(date, fileInfos);
            }
        }
        for (Map.Entry<String, List<FileInfo>> entry : tempMap.entrySet()) {
            String key = entry.getKey();
            List<FileInfo> photos = entry.getValue();
            Section headSection = new Section(true, key, photos.size());
            headSection.setChildNum(photos.size());
            datas.add(headSection);
            for (FileInfo photo : photos) {
                photo.setLastModified(key);
                datas.add(new Section(photo));
            }
        }
        return datas;

    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_test;
    }

    @Override
    protected void initWidget(View rootView) {
        mPhotoFrg = getMyParentFragment();
        mBottomSheetView = Objects.requireNonNull(getActivity()).findViewById(R.id.bottom_sheet);
        mRvPhotoList.setLayoutManager(new GridLayoutManager(mContext, 3));
        mRvPhotoList.addItemDecoration(new RecyclerViewItemDecoration(DisplayUtils.dip2px(getContext(), 5)));
        mRvPhotoList.getItemAnimator().setChangeDuration(0);
        mAdapter = new PhotoSectionAdapter(R.layout.item_rv_pic, R.layout.item_selction_head, mDatas);
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
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Section section = (Section) adapter.getData().get(position);
            if (!section.isHeader) {
                ImageView ivSelect = view.findViewById(R.id.iv_select);
                PicFile picFile = (PicFile) section.t;
                // 选中一个item
                if (!App.getTransferFileList().contains(picFile)) {
                    ivSelect.setVisibility(View.VISIBLE);
                    App.addTransferFile(picFile);
                    FilesStatusObservable.getInstance().notifyObservers(picFile,
                            TAG, FilesStatusObservable.FILE_SELECTED);
                    startFileSelectedAnimation(view);
                    updatePhotoAlbumCheckedStatus();
                    // 取消选中一个item
                } else {
                    ivSelect.setVisibility(View.INVISIBLE);
                    App.removeTransferFile(picFile);
                    FilesStatusObservable.getInstance().notifyObservers(picFile, TAG,
                            FilesStatusObservable.FILE_CANCEL_SELECTED);
                    updatePhotoAlbumCheckedStatus();
                }
                updateSelectionHead();
                updateSelectionHeadWhenItemClick(section);
            }
        });
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
                // 检查head的选中状态
                updateSelectionHead();
                mAdapter.notifyDataSetChanged();
            }
        });
        mFileStatusObserver = new AbsFileStatusObserver() {
            @Override
            public void onCancelSelected(FileInfo fileInfo) {
                mCheckBoxAll.setChecked(isSelectedAllPhotos());
                updateSelectionHead();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelSelectedAll(List<FileInfo> fileInfoList) {
                mCheckBoxAll.setChecked(isSelectedAllPhotos());
                updateSelectionHead();
                mAdapter.notifyDataSetChanged();
            }
        };
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {

            Section section = (Section) adapter.getItem(position);
            if (section.isHeader) {
                section.setCheckedAllChild(!section.isCheckedAllChild());
                String lastChanged = section.header;
                if (section.isCheckedAllChild()) {
                    for (Section mData : mDatas) {
                        if (!mData.isHeader) {
                            if (mData.t.getLastModified().equals(lastChanged)) {
                                App.addTransferFile(mData.t);
                                FilesStatusObservable.getInstance()
                                        .notifyObservers(mData.t, TAG,
                                                FilesStatusObservable.FILE_SELECTED);
                            }
                        }
                    }
                } else {
                    for (Section mData : mDatas) {
                        if (!mData.isHeader) {
                            if (mData.t.getLastModified().equals(lastChanged)) {
                                App.removeTransferFile(mData.t);
                                FilesStatusObservable.getInstance()
                                        .notifyObservers(mData.t, TAG,
                                                FilesStatusObservable.FILE_CANCEL_SELECTED);
                            }
                        }
                    }

                }

            }

            updatePhotoAlbumCheckedStatus();
            mAdapter.notifyDataSetChanged();


        });
        FilesStatusObservable.getInstance().register(TAG, mFileStatusObserver);
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
        // 动画开始后再将View还原为原始尺寸，此处先缓存下来
        int tempHeight = startView.getLayoutParams().height;
        int tempWidth = startView.getLayoutParams().width;
        startView.getLayoutParams().height = DisplayUtils.dip2px(getContext(), 130);
        startView.getLayoutParams().width = DisplayUtils.dip2px(getContext(), 130);
        if (getActivity() != null && (getActivity() instanceof SelectFilesActivity)) {
            targetView = mBottomSheetView;
        }
        AnimationUtils.setAddTaskAnimation(getActivity(), startView, targetView, null);
        startView.getLayoutParams().height = tempHeight;
        startView.getLayoutParams().width = tempWidth;
    }

    /**
     * 检查相册中的所有照片是否被全部选择
     *
     * @return
     */
    private boolean isSelectedAllPhotos() {
        for (Section section : mDatas) {
            if (!section.isHeader) {
                if (!App.getTransferFileList().contains(section.t)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        FilesStatusObservable.getInstance().remove(mFileStatusObserver);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
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

    public void updateSelectionHead() {
        if (mDatas == null) {
            return;
        }
        for (Section section : mDatas) {
            if (section.isHeader) {
                String headName = section.header;
                int unCheckedChildCount = 0;
                // 查找child
                for (Section s : mDatas) {
                    if (!s.isHeader) {
                        // 循环遍历每一组的子孩子，如果存在一个child和组名相同时并且没有被选中则将head的状态设置为未选中
                        if (s.t.getLastModified().equals(headName)) {
                            if (!App.getTransferFileList().contains(s.t)) {
                                unCheckedChildCount++;
                            }
                        }
                    }
                }
                Log.i("WW2K", "head: " + section.header + " - 未被选中的child的数量: " + unCheckedChildCount);
                // 这一组当中所有的child都处在一个选中的状态
                if (unCheckedChildCount == 0) {
                    section.setCheckedAllChild(true);
                } else {
                    section.setCheckedAllChild(false);
                }
            }
        }
    }

    private void updateSelectionHeadWhenItemClick(Section section) {

        String headName = "";
        if (section.t != null) {
            // 检查并更新head的选中状态
            headName = section.t.getLastModified();
            for (Section mData : mDatas) {
                if (!mData.isHeader) {
                    if (headName.equals(mData.t.getLastModified()) && !App.getTransferFileList()
                            .contains(mData.t)) {
                        for (Section data : mDatas) {
                            if (data.isHeader && data.header.equals(headName)) {
                                data.setCheckedAllChild(false);
                                int pos = mDatas.indexOf(data);
                                mAdapter.notifyItemChanged(pos);
                                return;
                            }
                        }
                    }
                }

            }
        }

        for (Section mData : mDatas) {
            if (mData.isHeader && mData.header.equals(headName)) {
                mData.setCheckedAllChild(true);
                int pos = mDatas.indexOf(mData);
                mAdapter.notifyItemChanged(pos);
                return;
            }
        }


    }

}
