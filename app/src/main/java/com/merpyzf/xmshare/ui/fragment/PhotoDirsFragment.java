package com.merpyzf.xmshare.ui.fragment;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.arch.lifecycle.LifecycleOwner;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.PhotoDirBean;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.receiver.FileSelectedListChangedReceiver;
import com.merpyzf.xmshare.ui.adapter.PhotoDirsAdapter;
import com.merpyzf.xmshare.ui.activity.OnFileSelectListener;
import com.merpyzf.xmshare.ui.activity.SearchActivity;
import com.merpyzf.xmshare.ui.widget.bean.Label;
import com.merpyzf.xmshare.util.PhotoUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * 设备中相册列表的展示
 *
 * @author wangke
 */

public class PhotoDirsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        BaseQuickAdapter.OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.rv_dirs_list)
    RecyclerView mRvDirsList;
    @BindView(R.id.pb_loading)
    ProgressBar mPbLoading;
    private LoaderManager mLoadManager;
    private CheckBox mCheckBoxAll;
    private List<PhotoDirBean> mPhotoDirs = new ArrayList<>();
    private OnFileSelectListener mFileSelectListener;
    private PhotoDirsAdapter mAdapter;
    private PhotoFragment mPhotoFragment;
    private int mPhotoNum;
    private static final String TAG = PhotoDirsFragment.class.getSimpleName();
    private long mStart;
    private long end;
    private View mEmptyView;
    private TextView mTvTip;
    private ImageView mIvSearch;
    private FileSelectedListChangedReceiver mFslcReceiver;

    public PhotoDirsFragment() {
    }

    @SuppressLint("ValidFragment")
    public PhotoDirsFragment(OnFileSelectListener fileSelectListener) {
        this.mFileSelectListener = fileSelectListener;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_img_dirs;
    }

    @Override
    protected void initEvent() {
        mAdapter.setOnItemClickListener(this);
        mCheckBoxAll.setOnCheckedChangeListener(this);
        // 当选择的文件列表发生改变时的回调
        mFslcReceiver = new FileSelectedListChangedReceiver() {
            @Override
            public void onFileListChanged(String unSelectedFile) {
                // 当选择的文件列表发生改变时的回调
                updatePhotoDirStatus(unSelectedFile);
                mAdapter.notifyDataSetChanged();
                mCheckBoxAll.setChecked(isCheckedAllDirs());
            }
        };

        Log.i("WKK", "initEvent方法执行了");
    }

    @Override
    protected void initWidget(View rootView) {
        super.initWidget(rootView);
        mIvSearch = getActivity().findViewById(R.id.iv_action_search);
        mEmptyView = View.inflate(mContext, R.layout.view_rv_file_empty, null);
        mTvTip = mEmptyView.findViewById(R.id.tv_empty);
        mTvTip.setText("扫描中...");
        mPhotoFragment = getMyParentFragment();
        mCheckBoxAll = getCheckBoxFromParentFrg();
        TextView tvTitle = getTextTitleFromParent();
        if (null != tvTitle) {
            tvTitle.setText("图片(" + mPhotoNum + ")");
        }
        mRvDirsList.setLayoutManager(new LinearLayoutManager(mContext));
        // TODO: 2018/4/2 分割线需要美化
        // 添加分割线前先移除之前添加的
        removeItemDecoration();
        mRvDirsList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mAdapter = new PhotoDirsAdapter(R.layout.item_rv_photo_dir, mCheckBoxAll, mPhotoDirs, mFileSelectListener);
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
            initLoadManager();
        }
    }

    /**
     * 初始化LoadManager
     */
    private void initLoadManager() {
        mLoadManager = Objects.requireNonNull(getActivity()).getLoaderManager();
        mLoadManager.initLoader(FileInfo.FILE_TYPE_IMAGE, null, PhotoDirsFragment.this);
    }

    /**
     * 动态注册监听选择文件列表发生改变的广播
     */
    private void registerFileListChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileSelectedListChangedReceiver.ACTION);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mFslcReceiver, intentFilter);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == FileInfo.FILE_TYPE_IMAGE) {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projections = new String[]
                    {
                            MediaStore.Images.Media.DATA,
                    };
            return new CursorLoader(getContext(), uri, projections, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
        }
        return null;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        PhotoUtils.AsyncLoadingFromCourse(data)
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(photoDirBeans -> {
                    if (photoDirBeans.size() == 0) {
                        mTvTip.setText("未扫描到任何文件");
                    }
                    mPhotoDirs.clear();
                    mPhotoDirs.addAll(photoDirBeans);
                    mPbLoading.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                    PhotoUtils.getNumberOfPhotos(mPhotoDirs)
                            .subscribe(photoNum -> {
                                if (getTextTitleFromParent() != null) {
                                    mPhotoNum = photoNum;
                                    getTextTitleFromParent().setText("图片(" + mPhotoNum + ")");
                                }
                            });
                });
    }

    @Override
    public void onLoaderReset(Loader loader) {

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
        registerFileListChangedReceiver();
        mCheckBoxAll.setTag(TAG);
        mAdapter.notifyDataSetChanged();
        // 设置当前所有相册选中状态
        TextView tvTitle = getTextTitleFromParent();
        if (null != tvTitle) {
            tvTitle.setText("图片(" + mPhotoNum + ")");
        }
        Log.i("WKK", "是否选择全部目录--> " + isCheckedAllDirs());
        mCheckBoxAll.setChecked(isCheckedAllDirs());


    }

    /**
     * 判断所有相册是否被选中
     */
    private boolean isCheckedAllDirs() {
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

    private void updatePhotoDirStatus(String filePath) {
        if (null == mPhotoDirs || mPhotoDirs.size() == 0) {
            return;
        }
        for (PhotoDirBean photoDir : mPhotoDirs) {
            for (FileInfo fileInfo : photoDir.getImageList()) {
                Log.i("WK", "s-path==> "+fileInfo.getPath());
                if (filePath.equals(fileInfo.getPath())) {
                    photoDir.setChecked(false);
                    break;
                }

            }
        }
    }


    /**
     * 从父Fragment中获取CheckBox控件
     */
    private CheckBox getCheckBoxFromParentFrg() {
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.i("WKK", "onCheckedChanged");
        if (buttonView.getTag().equals(TAG)) {
            // 全选
            if (isChecked) {
                for (PhotoDirBean photoDir : mPhotoDirs) {
                    photoDir.setChecked(true);
                    mFileSelectListener.onSelectedAll(photoDir.getImageList());
                }
                mAdapter.notifyDataSetChanged();
                mPhotoFragment.getTvChecked().setText("取消全选");
            } else {
                if (isCheckedAllDirs()) {
                    for (PhotoDirBean photoDir : mPhotoDirs) {
                        photoDir.setChecked(false);
                        mFileSelectListener.onCancelSelectedAll(photoDir.getImageList());
                    }
                }
                mAdapter.notifyDataSetChanged();
                mPhotoFragment.getTvChecked().setText("全选");
            }
        }

    }


    public TextView getTextTitleFromParent() {
        if (null == mPhotoFragment) {
            return null;
        }
        return mPhotoFragment.getTvTitle();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mIvSearch.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                Bundle bundle = new Bundle();
                Log.i("W2k", "加载的文件类型-> " + "图片");
                //bundle.putSerializable("fileList", (Serializable) mFileLists);
                //intent.putExtras(bundle);
                getActivity().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mFslcReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mLoadManager.destroyLoader(FileInfo.FILE_TYPE_IMAGE);
        super.onDestroy();
    }

}
