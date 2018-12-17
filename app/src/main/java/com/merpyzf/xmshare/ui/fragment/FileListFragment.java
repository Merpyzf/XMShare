package com.merpyzf.xmshare.ui.fragment;

import android.annotation.SuppressLint;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.merpyzf.common.utils.DisplayUtils;
import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.common.FileLoadManager;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.observer.AbsFileStatusObserver;
import com.merpyzf.xmshare.observer.FilesStatusObservable;
import com.merpyzf.xmshare.ui.adapter.FileAdapter;
import com.merpyzf.xmshare.ui.activity.SelectFilesActivity;
import com.merpyzf.xmshare.ui.widget.RecyclerViewItemDecoration;
import com.merpyzf.xmshare.ui.widget.tools.CustomRecyclerScrollViewListener;
import com.merpyzf.xmshare.util.AnimationUtils;
import com.merpyzf.xmshare.util.ApkUtils;
import com.merpyzf.xmshare.util.CacheUtils;
import com.merpyzf.xmshare.util.CollectionUtils;
import com.merpyzf.xmshare.util.MusicUtils;
import com.merpyzf.xmshare.util.VideoUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static com.merpyzf.transfermanager.entity.BaseFileInfo.FILE_TYPE_APP;
import static com.merpyzf.transfermanager.entity.BaseFileInfo.FILE_TYPE_MUSIC;
import static com.merpyzf.transfermanager.entity.BaseFileInfo.FILE_TYPE_VIDEO;

/**
 * 扫描到的本地文件列表的展示页面
 * 扫描: 应用、音乐、视频
 *
 * @author wangke
 */
public class FileListFragment extends BaseFragment {

    @BindView(R.id.rv_music_list)
    FastScrollRecyclerView mRvFileList;
    @BindView(R.id.pb_loading)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.checkbox_all)
    CheckBox mCheckBoxAll;
    @BindView(R.id.tv_checked)
    TextView mTvChecked;

    private int mLoadFileType;
    private CopyOnWriteArrayList<BaseFileInfo> mFileList = new CopyOnWriteArrayList<>();
    private FileAdapter mFileListAdapter;
    private View mBottomSheetView;
    private FileLoadManager mFileLoadManager;
    private String TAG = FileListFragment.class.getSimpleName();
    private MyAbsFileStatusObserver mFileStatusObserver;
    private CustomRecyclerScrollViewListener mScrollListener;

    public static FileListFragment newInstance(int type) {
        return new FileListFragment(type);
    }

    @SuppressLint("ValidFragment")
    public FileListFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    private FileListFragment(int type) {
        super();
        this.mLoadFileType = type;
        TAG += type;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_file_list;
    }

    @Override
    protected void doCreateView(View rootView) {
        mBottomSheetView = getActivity().findViewById(R.id.bottom_sheet);
        updateCbxTitle();
        initRecyclerView();
    }

    @Override
    protected void doCreateEvent() {
        if (mFileListAdapter == null) {
            return;
        }
        if (mScrollListener != null) {
            mRvFileList.addOnScrollListener(mScrollListener);
        }
        mFileListAdapter.setOnItemClickListener((adapter, view, position) -> {
            ImageView ivSelect = view.findViewById(R.id.iv_select);
            BaseFileInfo fileInfo = mFileList.get(position);
            if (!App.getTransferFileList().contains(fileInfo)) {
                ivSelect.setVisibility(View.VISIBLE);
                // 添加选中的文件
                App.addTransferFile(fileInfo);
                // 将文件选择的事件回调给外部
                FilesStatusObservable.getInstance().notifyObservers(fileInfo, TAG,
                        FilesStatusObservable.FILE_SELECTED);
                View startView = view.findViewById(R.id.iv_cover);
                View targetView = null;
                if (getActivity() != null && (getActivity() instanceof SelectFilesActivity)) {
                    targetView = mBottomSheetView;
                }
                // 动画开始后再将View还原为原始尺寸，此处先缓存下来
                int tempHeight = startView.getLayoutParams().height;
                int tempWidth = startView.getLayoutParams().width;
                startView.getLayoutParams().height = DisplayUtils.dip2px(getContext(), 100);
                startView.getLayoutParams().width = DisplayUtils.dip2px(getContext(), 100);
                AnimationUtils.setAddTaskAnimation(getActivity(), startView, targetView, null);
                startView.getLayoutParams().height = tempHeight;
                startView.getLayoutParams().width = tempWidth;
            } else {
                // 将选择文件的事件回调给外部
                ivSelect.setVisibility(View.INVISIBLE);
                App.removeTransferFile(fileInfo);
                FilesStatusObservable.getInstance()
                        .notifyObservers(fileInfo, TAG,
                                FilesStatusObservable.FILE_CANCEL_SELECTED);
            }
            mCheckBoxAll.setChecked(isSelectedAllFile());
        });
        mFileStatusObserver = new MyAbsFileStatusObserver();
        // 监听已选传输文件列表的变化
        FilesStatusObservable.getInstance()
                .register(TAG, mFileStatusObserver);
        // 顶部全选按钮的事件
        mCheckBoxAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 将全选事件回调给外界
            if (isChecked) {
                mTvChecked.setText("取消全选");
                FilesStatusObservable.getInstance()
                        .notifyObservers(mFileList, TAG,
                                FilesStatusObservable.FILE_SELECTED_ALL);
                mFileListAdapter.notifyDataSetChanged();
            } else {
                mTvChecked.setText("全选");
                if (isSelectedAllFile()) {
                    FilesStatusObservable.getInstance()
                            .notifyObservers(mFileList, TAG,
                                    FilesStatusObservable.FILE_CANCEL_SELECTED_ALL);
                    mFileListAdapter.notifyDataSetChanged();
                }

            }

        });
    }

    @Override
    protected void initData() {
        super.initData();
        mFileLoadManager = new FileLoadManager(getActivity(), mLoadFileType) {
            @SuppressLint("CheckResult")
            @Override
            public void onLoadFinished(Observable observable) {
                observable = observable.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW));
                if (mLoadFileType == FILE_TYPE_APP) {
                    observable.subscribe((Consumer<List<ApkFile>>) appFiles -> {
                        // 缓存设备中的应用信息到本地数据库，以便于用户查询
                        CacheUtils.cacheAppInfo(mContext, appFiles);
                        mFileList.addAll(appFiles);
                        CollectionUtils.shortingByFirstCase(mFileList);
                        updateTitle(Const.PAGE_APP_TITLE);
                        notifyRvDataChanged();
                        ApkUtils.asyncCacheApkIco(mContext, appFiles);
                    });
                } else if (mLoadFileType == FILE_TYPE_MUSIC) {
                    observable.subscribe((Consumer<List<BaseFileInfo>>) musicFiles -> {
                        if (musicFiles.size() == 0) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "没有扫描到音乐文件", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mFileList.size() == 0) {
                            mFileList.addAll(musicFiles);
                        }
                        CollectionUtils.shortingByFirstCase(mFileList);
                        updateTitle(Const.PAGE_MUSIC_TITLE);
                        MusicUtils.asyncUpdateAlbumImg(getContext(), musicFiles);
                        notifyRvDataChanged();
                    });
                } else if (mLoadFileType == FILE_TYPE_VIDEO) {
                    observable.subscribe((Consumer<List<BaseFileInfo>>) videoFiles -> {
                        if (videoFiles.size() == 0) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "没有扫描到视频文件", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mFileList.size() == 0) {
                            mFileList.addAll(videoFiles);
                        }
                        CollectionUtils.shortingByFirstCase(mFileList);
                        updateTitle(Const.PAGE_VIDEO_TITLE);
                        VideoUtils.asyncUpdateThumb(getContext(), videoFiles);
                        notifyRvDataChanged();
                    });

                }
            }
        };
    }

    private void initRecyclerView() {
        switch (mLoadFileType) {
            case FILE_TYPE_APP:
                updateTitle("应用");
                mRvFileList.setLayoutManager(new GridLayoutManager(getContext(), 4));
                mFileListAdapter = new FileAdapter<>(getActivity(), R.layout.item_rv_apk, mFileList);
                break;

            case FILE_TYPE_MUSIC:
                updateTitle("音乐");
                mRvFileList.setLayoutManager(new GridLayoutManager(getContext(), 3));
                mRvFileList.addItemDecoration(new RecyclerViewItemDecoration(DisplayUtils.
                        dip2px(getContext(), 5)));
                mFileListAdapter = new FileAdapter<>(getActivity(), R.layout.item_rv_music, mFileList);
                break;

            case FILE_TYPE_VIDEO:
                updateTitle("视频");
                mRvFileList.setLayoutManager(new GridLayoutManager(getContext(), 3));
                mRvFileList.addItemDecoration(new RecyclerViewItemDecoration(DisplayUtils.
                        dip2px(getContext(), 5)));
                mFileListAdapter = new FileAdapter<>(getActivity(), R.layout.item_rv_video, mFileList);
                break;

            default:
                break;
        }
        View emptyView = View.inflate(mContext, R.layout.view_rv_file_empty, null);
        if (emptyView != null) {
            if (mFileListAdapter == null) {
                return;
            }
            mFileListAdapter.setEmptyView(emptyView);
            mRvFileList.setAdapter(mFileListAdapter);
        }
    }

    private void updateCbxTitle() {
        if (mCheckBoxAll.isChecked()) {
            mTvChecked.setText("取消全选");
        } else {
            mTvChecked.setText("全选");
        }
    }

    private void notifyRvDataChanged() {
        mRvFileList.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mFileListAdapter.notifyDataSetChanged();

    }

    public void updateTitle(String type) {
        mTvTitle.setText(type + "(" + mFileList.size() + ")");
    }

    private boolean isSelectedAllFile() {
        for (BaseFileInfo fileInfo : mFileList) {
            if (!App.getTransferFileList().contains(fileInfo)) {
                return false;
            }
        }
        return true;
    }

    public void setScrollListener(CustomRecyclerScrollViewListener scrollListener) {
        this.mScrollListener = scrollListener;
    }

    @Override
    public void onDestroy() {
        mFileLoadManager.destroyLoader();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        mFileLoadManager.destroyLoader();
        FilesStatusObservable.getInstance().remove(mFileStatusObserver);
        super.onDestroyView();
    }

    class MyAbsFileStatusObserver extends AbsFileStatusObserver {
        @Override
        public void onCancelSelectedAll(List<BaseFileInfo> fileInfoList) {
            mFileListAdapter.notifyDataSetChanged();
            mCheckBoxAll.setChecked(isSelectedAllFile());
        }

        @Override
        public void onCancelSelected(BaseFileInfo fileInfo) {
            // 当选择的文件列表发生改变时的回调
            mFileListAdapter.notifyDataSetChanged();
            mCheckBoxAll.setChecked(isSelectedAllFile());
        }
    }
}
