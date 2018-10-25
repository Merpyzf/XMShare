package com.merpyzf.xmshare.ui.fragment;


import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.PhotoDirBean;
import com.merpyzf.xmshare.common.FileLoadManager;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.observer.AbsFileStatusObserver;
import com.merpyzf.xmshare.observer.FilesStatusObservable;
import com.merpyzf.xmshare.ui.adapter.PhotoDirsAdapter;
import com.merpyzf.xmshare.ui.widget.bean.Label;
import com.merpyzf.xmshare.util.PhotoUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static com.merpyzf.fileserver.common.bean.FileInfo.FILE_TYPE_IMAGE;

/**
 * 设备中相册列表的展示
 *
 * @author wangke
 */

public class PhotoDirsFragment extends BaseFragment implements
        BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.rv_dirs_list)
    RecyclerView mRvDirsList;
    @BindView(R.id.pb_loading)
    ProgressBar mPbLoading;
    private CheckBox mCheckBoxAll;
    private List<PhotoDirBean> mPhotoDirs = new ArrayList<>();
    private PhotoDirsAdapter mAdapter;
    private PhotoFragment mPhotoFragment;
    private int mPhotoNum;
    private static final String TAG = PhotoDirsFragment.class.getSimpleName();
    private View mEmptyView;
    private TextView mTvTip;
    private AbsFileStatusObserver mFileStatusObservable;
    private FileLoadManager mFileLoadManager;

    public PhotoDirsFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_img_dirs;
    }

    @Override
    protected void initEvent() {
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            PhotoDirBean item = (PhotoDirBean) adapter.getItem(position);
            if (item.isChecked()) {
                FilesStatusObservable.getInstance().notifyObservers(item.getImageList(),
                        TAG, FilesStatusObservable.FILE_CANCEL_SELECTED_ALL);
            } else {
                FilesStatusObservable.getInstance().notifyObservers(item.getImageList(),
                        TAG, FilesStatusObservable.FILE_SELECTED_ALL);
            }
            item.setChecked(!item.isChecked());
            mCheckBoxAll.setChecked(isCheckAllDirs());
            mAdapter.notifyItemChanged(position);
        });
        mCheckBoxAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.getTag().equals(TAG)) {
                if (isChecked) {
                    for (PhotoDirBean photoDir : mPhotoDirs) {
                        photoDir.setChecked(true);
                        FilesStatusObservable.getInstance().notifyObservers(photoDir.getImageList(), TAG,
                                FilesStatusObservable.FILE_SELECTED_ALL
                        );
                    }
                    mAdapter.notifyDataSetChanged();
                    mPhotoFragment.getTvChecked().setText("取消全选");
                } else {
                    if (isSelectedAllAlbum()) {
                        for (PhotoDirBean photoDir : mPhotoDirs) {
                            photoDir.setChecked(false);
                            FilesStatusObservable.getInstance().notifyObservers(photoDir.getImageList(), TAG,
                                    FilesStatusObservable.FILE_CANCEL_SELECTED_ALL
                            );
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    mPhotoFragment.getTvChecked().setText("全选");
                }
            }
        });
        mFileStatusObservable = new AbsFileStatusObserver() {
            @Override
            public void onCancelSelected(FileInfo fileInfo) {
                mAdapter.notifyDataSetChanged();
                mCheckBoxAll.setChecked(isSelectedAllAlbum());
            }

            @Override
            public void onCancelSelectedAll(List<FileInfo> fileInfoList) {
                mCheckBoxAll.setChecked(isCheckAllDirs());
                mAdapter.notifyDataSetChanged();
            }
        };
        FilesStatusObservable.getInstance().register(TAG, mFileStatusObservable);
    }


    private boolean isCheckAllDirs() {
        for (PhotoDirBean photoDirBean : mPhotoDirs) {
            if (!photoDirBean.isChecked()) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected void initWidget(View rootView) {
        super.initWidget(rootView);
        mEmptyView = View.inflate(mContext, R.layout.view_rv_file_empty, null);
        mTvTip = mEmptyView.findViewById(R.id.tv_empty);
        mTvTip.setText("扫描中...");
        mPhotoFragment = getMyParentFragment();
        mCheckBoxAll = getCbxFromParentFrg();
        TextView tvTitle = getTextViewFromParentFrg();
        if (null != tvTitle) {
            tvTitle.setText("图片(" + mPhotoNum + ")");
        }
        mRvDirsList.setLayoutManager(new LinearLayoutManager(mContext));
        // TODO: 2018/4/2 分割线需要美化
        // 添加分割线前先移除之前添加的
        removeItemDecoration();
        mRvDirsList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mAdapter = new PhotoDirsAdapter(R.layout.item_rv_photo_dir, mPhotoDirs);
        mAdapter.setEmptyView(mEmptyView);
        mRvDirsList.setAdapter(mAdapter);
    }

    private void removeItemDecoration() {
        int itemDecorationCount = mRvDirsList.getItemDecorationCount();
        for (int i = 0; i < itemDecorationCount; i++) {
            mRvDirsList.removeItemDecorationAt(i);
        }
    }


    @Override
    protected void initData() {
        // 只需要在此Fragment创建的时候加载一次数据，在它上面的fragment在退栈的时候会导致当前的fragment重新创建一次View
        if (mPhotoDirs.size() == 0) {
            mFileLoadManager = new FileLoadManager(getActivity(), FILE_TYPE_IMAGE) {
                @SuppressLint("CheckResult")
                @Override
                public void onLoadFinished(Observable observable) {
                    observable.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                            .subscribe((Consumer<List<PhotoDirBean>>) photoDirBeans -> {
                                if (photoDirBeans.size() == 0) {
                                    mTvTip.setText("未扫描到任何文件");
                                }
                                mPhotoDirs.clear();
                                mPhotoDirs.addAll(photoDirBeans);
                                mPbLoading.setVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();
                                PhotoUtils.getNumberOfPhotos(mPhotoDirs)
                                        .subscribe(photoNum -> {
                                            if (getTextViewFromParentFrg() != null) {
                                                mPhotoNum = photoNum;
                                                getTextViewFromParentFrg().setText("图片(" + mPhotoNum + ")");
                                            }
                                        });
                            });
                }
            };
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity())
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("xm");
        PhotoDirBean item = (PhotoDirBean) adapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("photos", item);
        fragmentTransaction.replace(R.id.fl_container, ShowPhotosFragment.getInstance(bundle));
        fragmentTransaction.commit();
        mPhotoFragment.getFileSelectIndicator().add(new Label(item.getName(), ""));
    }

    @Override
    public void onResume() {
        super.onResume();
        mCheckBoxAll.setTag(TAG);
        mAdapter.notifyDataSetChanged();
        TextView tvTitle = getTextViewFromParentFrg();
        if (null != tvTitle) {
            tvTitle.setText("图片(" + mPhotoNum + ")");
        }
        mCheckBoxAll.setChecked(isSelectedAllAlbum());
    }

    /**
     * 判断所有相册是否被选中
     */
    private boolean isSelectedAllAlbum() {
        if (null == mPhotoDirs || mPhotoDirs.size() == 0) {
            return false;
        }
        for (PhotoDirBean photoDir : mPhotoDirs) {
            if (!photoDir.isChecked()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 从父Fragment中获取CheckBox控件
     */
    private CheckBox getCbxFromParentFrg() {
        CheckBox checkBox = null;
        if (null == mPhotoFragment) {
            return null;
        }
        checkBox = mPhotoFragment.getCheckbox();
        if (null != checkBox) {
            checkBox.setTag(TAG);
        }
        return checkBox;
    }

    /**
     * 获取父Fragment
     */
    private PhotoFragment getMyParentFragment() {
        PhotoFragment photoFragment = null;
        List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof PhotoFragment) {
                photoFragment = (PhotoFragment) fragment;
            }
        }
        return photoFragment;
    }

    public TextView getTextViewFromParentFrg() {
        if (null == mPhotoFragment) {
            return null;
        }
        return mPhotoFragment.getTvTitle();
    }

    @Override
    public void onDestroy() {
        mFileLoadManager.destroyLoader();
        FilesStatusObservable.getInstance().remove(mFileStatusObservable);
        super.onDestroy();
    }

}
