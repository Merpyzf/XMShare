package com.merpyzf.xmshare.ui.fragment.filemanager;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.StorageFile;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.observer.FilesStatusObservable;
import com.merpyzf.xmshare.observer.FilesStatusObserver;
import com.merpyzf.xmshare.ui.adapter.FileManagerAdapter;
import com.merpyzf.xmshare.ui.fragment.FunctionListFragment;
import com.merpyzf.xmshare.ui.widget.IndicatorChangedCallback;
import com.merpyzf.xmshare.ui.widget.RecyclerViewDivider;
import com.merpyzf.xmshare.ui.widget.SelectIndicatorView;
import com.merpyzf.xmshare.ui.widget.bean.Indicator;
import com.merpyzf.xmshare.ui.widget.tools.CustomRecyclerScrollViewListener;
import com.merpyzf.xmshare.util.FileTypeHelper;
import com.merpyzf.xmshare.util.FileUtils;
import com.merpyzf.xmshare.util.SettingHelper;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.merpyzf.xmshare.util.UiUtils.getRecyclerViewLastPosition;

/**
 * @author wangke
 */
public class FileManagerFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, IndicatorChangedCallback, BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R.id.rv_file_list)
    RecyclerView mRvFileList;
    @BindView(R.id.select_indicator)
    SelectIndicatorView mSelectIndicator;
    @BindView(R.id.view_underline)
    View mViewUnderLine;
    @BindView(R.id.checkbox_all)
    CheckBox mCheckAll;
    @BindView(R.id.tv_checked)
    TextView mTvChecked;
    private String mRootPath;
    private String mVolumeName;
    private List<StorageFile> mFileList;
    private FileManagerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Indicator mCurrIndicator;
    private CustomRecyclerScrollViewListener mScrollListener;
    private static String TAG = FileManagerFragment.class.getSimpleName();

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mRootPath = (String) bundle.getCharSequence("rootPath");
        mVolumeName = (String) bundle.getCharSequence("volumeName");
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_file_manager;
    }

    @Override
    protected void initWidget(View rootView) {
        super.initWidget(rootView);
        mSelectIndicator.addIndicator(new Indicator(mVolumeName, mRootPath));
        mLayoutManager = new LinearLayoutManager(mContext);
        mRvFileList.setLayoutManager(mLayoutManager);
        mRvFileList.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL));
        mFileList = new ArrayList<>();
        mAdapter = new FileManagerAdapter(R.layout.item_fileinfo, mFileList);
        View emptyView = View.inflate(mContext, R.layout.view_rv_file_empty, null);
        mAdapter.setEmptyView(emptyView);
        mRvFileList.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        loadingDirectory(mRootPath);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initEvent() {
        super.initEvent();
        if (mScrollListener != null) {
            mRvFileList.addOnScrollListener(mScrollListener);
        }
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
        mSelectIndicator.setIndicatorClickCallBack(this);
        mRvFileList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mRvFileList.canScrollVertically(-1)) {
                    if (mViewUnderLine.getVisibility() == View.INVISIBLE) {
                        mViewUnderLine.setVisibility(View.VISIBLE);
                    }
                } else {
                    mViewUnderLine.setVisibility(View.INVISIBLE);
                }

            }
        });
        mCheckAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mTvChecked.setText("取消全选");
                Observable.fromIterable(mFileList)
                        .filter(storageFile -> !storageFile.isDirectory())
                        .subscribe(storageFile -> {
                            FilesStatusObservable.getInstance()
                                    .notifyObservers(storageFile, TAG, FilesStatusObservable.FILE_SELECTED);
                        });
            } else {
                mTvChecked.setText("全选");
                if (isSelectedAllFile()) {
                    Observable.fromIterable(mFileList)
                            .filter(storageFile -> !storageFile.isDirectory())
                            .subscribe(storageFile -> {
                                if (App.isContain(storageFile)) {
                                    FilesStatusObservable.getInstance()
                                            .notifyObservers(storageFile, TAG, FilesStatusObservable.FILE_CANCEL_SELECTED);
                                }
                            });
                }
            }
            mAdapter.notifyDataSetChanged();
        });
        FilesStatusObservable.getInstance().register(TAG, new FilesStatusObserver() {
            @Override
            public void onSelected(BaseFileInfo fileInfo) {
                mCheckAll.setChecked(isSelectedAllFile());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelSelected(BaseFileInfo fileInfo) {
                mCheckAll.setChecked(isSelectedAllFile());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSelectedAll(List<BaseFileInfo> fileInfoList) {
                mCheckAll.setChecked(isSelectedAllFile());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelSelectedAll(List<BaseFileInfo> fileInfoList) {
                mCheckAll.setChecked(isSelectedAllFile());
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 加载目录中的文件
     *
     * @param dir
     */
    public void loadingDirectory(String dir) {
        File file = new File(dir);
        ArrayList<StorageFile> tempDirs = new ArrayList();
        ArrayList<StorageFile> tempFiles = new ArrayList();
        if (mFileList.size() != 0) {
            mFileList.clear();
        }
        File[] files = file.listFiles();
        Observable.fromArray(files)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .filter(storageFile -> {
                    // 根据配置选择是否过滤隐藏文件
                    boolean isShow = SettingHelper.showHiddenFile(mContext);
                    if (!isShow) {
                        if (storageFile.getName().startsWith(".")) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(storageFile -> {
                    StorageFile fileInfo = new StorageFile();
                    fileInfo.setDirectory(storageFile.isDirectory());
                    fileInfo.setName(storageFile.getName());
                    fileInfo.setPath(storageFile.getPath());
                    String suffix = FileUtils.getFileSuffix(fileInfo.getPath()).toLowerCase();
                    fileInfo.setPhoto(FileTypeHelper.isPhotoType(suffix));
                    fileInfo.setLength(storageFile.length());
                    return fileInfo;
                }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<StorageFile>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(StorageFile storageFile) {
                        if (storageFile.isDirectory()) {
                            setFileAndFolderNum(storageFile);
                            tempDirs.add(storageFile);
                        } else {
                            tempFiles.add(storageFile);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Collections.sort(tempDirs, (o1, o2) -> {
                            if (o1.getFirstLetter() > o2.getFirstLetter()) {
                                return 1;
                            } else if (o1.getFirstLetter() < o2.getFirstLetter()) {
                                return -1;
                            } else {
                                return 0;
                            }
                        });
                        Collections.sort(tempFiles, (o1, o2) -> {
                            if (o1.getFirstLetter() > o2.getFirstLetter()) {
                                return 1;
                            } else if (o1.getFirstLetter() < o2.getFirstLetter()) {
                                return -1;
                            } else {
                                return 0;
                            }
                        });
                        // 按照文件夹在前文件在后的顺序添加到集合中去
                        mFileList.addAll(tempDirs);
                        mFileList.addAll(tempFiles);
                        mCheckAll.setChecked(isSelectedAllFile());
                        mAdapter.notifyDataSetChanged();
                        if (mCurrIndicator != null) {
                            int[] lastPos = (int[]) mCurrIndicator.getTag();
                            mLayoutManager.scrollToPositionWithOffset(lastPos[0], lastPos[1]);
                        }

                    }
                });
    }

    /**
     * 统计一个目录下所包含的文件和文件夹的个数，并设置给StorageFile对象
     *
     * @param storageFile
     */
    private void setFileAndFolderNum(StorageFile storageFile) {
        int fileNum = 0;
        int folderNum = 0;
        File file = new File(storageFile.getPath());
        File[] files = file.listFiles(f -> {
            boolean isShowHidden = SettingHelper.showHiddenFile(mContext);
            if (isShowHidden) {
                return true;
            } else {
                if (f.getName().startsWith(".")) {
                    return false;
                } else {
                    return true;
                }
            }
        });

        for (File f : files) {
            if (f.isFile()) {
                ++fileNum;
            }
            if (f.isDirectory()) {
                ++folderNum;
            }
        }
        storageFile.setFileNum(fileNum);
        storageFile.setFolderNum(folderNum);
    }

    @Override
    public void onIndicatorChanged(Indicator indicator) {
        loadingDirectory(indicator.getValue());
        mCurrIndicator = indicator;

    }

    public void onBackPressed() {
        if (mSelectIndicator.isInRoot()) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fl_main_container, new FunctionListFragment());
            transaction.commit();
        } else {
            mSelectIndicator.pop();
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        StorageFile fileInfo = (StorageFile) adapter.getItem(position);
        if (fileInfo.isDirectory()) {
            if (App.isContain(fileInfo)) {
                App.removeTransferFileByPath(fileInfo.getPath());
                mAdapter.notifyItemChanged(position);
            } else {
                loadingDirectory(fileInfo.getPath());
                Indicator indicator = new Indicator(fileInfo.getName(), fileInfo.getPath());
                int[] lastPosition = getRecyclerViewLastPosition(mLayoutManager);
                mSelectIndicator.setTagInIndicator(lastPosition);
                mSelectIndicator.addIndicator(indicator);
            }
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        StorageFile storageFile = (StorageFile) adapter.getItem(position);
        // 暂且先把传输整个文件夹的功能移除！！！
        if (!storageFile.isDirectory()) {
            if (App.isContain(storageFile)) {
                App.removeTransferFileByPath(storageFile.getPath());
                FilesStatusObservable.getInstance().notifyObservers((StorageFile) null, TAG, FilesStatusObservable.FILE_CANCEL_SELECTED);
            } else {
                App.addTransferFile(storageFile);
                // 将文件选择的事件回调给外部
                FilesStatusObservable.getInstance().notifyObservers(storageFile, TAG, FilesStatusObservable.FILE_SELECTED);
            }
            mCheckAll.setChecked(isSelectedAllFile());
        }
        mAdapter.notifyItemChanged(position);
    }

    public void setScrollListener(CustomRecyclerScrollViewListener scrollListener) {
        this.mScrollListener = scrollListener;
    }

    public boolean isSelectedAllFile() {
        if (mFileList.size() == 0) {
            mCheckAll.setEnabled(false);
            return false;
        }
        List<BaseFileInfo> tempList = new ArrayList<>();
        for (StorageFile storageFile : mFileList) {
            if (!storageFile.isDirectory()) {
                tempList.add(storageFile);
            }
        }

        if (tempList.size() == 0) {
            mCheckAll.setEnabled(false);
            return false;
        }
        mCheckAll.setEnabled(true);
        for (BaseFileInfo f : tempList) {
            if (!App.isContain(f)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onDestroy() {
        FilesStatusObservable.getInstance().remove(TAG);
        super.onDestroy();
    }
}
