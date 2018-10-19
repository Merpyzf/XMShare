package com.merpyzf.xmshare.ui.fragment;

import android.annotation.SuppressLint;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.common.FileLoadManager;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.observer.AbsFileStatusObserver;
import com.merpyzf.xmshare.observer.FilesStatusObservable;
import com.merpyzf.xmshare.ui.adapter.FileAdapter;
import com.merpyzf.xmshare.ui.activity.SelectFilesActivity;
import com.merpyzf.xmshare.util.AnimationUtils;
import com.merpyzf.xmshare.util.ApkUtils;
import com.merpyzf.xmshare.util.CollectionUtils;
import com.merpyzf.xmshare.util.Md5Utils;
import com.merpyzf.xmshare.util.MusicUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_APP;
import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_MUSIC;
import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_VIDEO;

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
    private List<FileInfo> mFileLists = new ArrayList<>();
    private FileAdapter mFileListAdapter;
    private View mBottomSheetView;
    private FileLoadManager mFileLoadManager;
    private String TAG = FileListFragment.class.getSimpleName();

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
    protected void initWidget(View rootView) {
        mBottomSheetView = getActivity().findViewById(R.id.bottom_sheet);
        updateCbxTitle();
        initRecyclerView();
    }

    @Override
    protected void initEvent() {

        mFileListAdapter.setOnItemClickListener((adapter, view, position) -> {
            ImageView ivSelect = view.findViewById(R.id.iv_select);
            FileInfo fileInfo = mFileLists.get(position);
            if (!App.getSendFileList().contains(fileInfo)) {
                ivSelect.setVisibility(View.VISIBLE);
                // 添加选中的文件
                App.addSendFile(fileInfo);
                fileInfo.setMd5(Md5Utils.getFileMd5(fileInfo));
                // 将文件选择的事件回调给外部
                FilesStatusObservable.getInstance().notifyObservers(fileInfo, TAG,
                        FilesStatusObservable.FILE_SELECTED);
                View startView = view.findViewById(R.id.iv_cover);
                View targetView = null;
                if (getActivity() != null && (getActivity() instanceof SelectFilesActivity)) {
                    targetView = mBottomSheetView;
                }
                AnimationUtils.setAddTaskAnimation(getActivity(), startView, targetView, null);

            } else {
                // 将选择文件的事件回调给外部
                ivSelect.setVisibility(View.INVISIBLE);
                App.removeSendFile(fileInfo);
                FilesStatusObservable.getInstance()
                        .notifyObservers(fileInfo, TAG,
                                FilesStatusObservable.FILE_CANCEL_SELECTED);
            }
            mCheckBoxAll.setChecked(isSelectedAllFile());
        });
        // 监听已选传输文件列表的变化
        FilesStatusObservable.getInstance()
                .register(TAG, new AbsFileStatusObserver() {
                    @Override
                    public void onCancelSelectedAll(List<FileInfo> fileInfoList) {
                        mFileListAdapter.notifyDataSetChanged();
                        mCheckBoxAll.setChecked(isSelectedAllFile());
                    }

                    @Override
                    public void onCancelSelected(FileInfo fileInfo) {
                        // 当选择的文件列表发生改变时的回调
                        mFileListAdapter.notifyDataSetChanged();
                        mCheckBoxAll.setChecked(isSelectedAllFile());
                    }
                });
        // 顶部全选按钮的事件
        mCheckBoxAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 将全选事件回调给外界
            if (isChecked) {
                mTvChecked.setText("取消全选");
                FilesStatusObservable.getInstance()
                        .notifyObservers(mFileLists, TAG,
                                FilesStatusObservable.FILE_SELECTED_ALL);
                mFileListAdapter.notifyDataSetChanged();
            } else {
                //将取消全选的事件回调给外部
                if (isSelectedAllFile()) {
                    mTvChecked.setText("全选");
                    FilesStatusObservable.getInstance()
                            .notifyObservers(mFileLists, TAG,
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
                    observable.subscribe((Consumer<List<ApkFile>>) apkFiles -> {
                        for (ApkFile apkFile : apkFiles) {
                            Log.i("WW2k", apkFile.getPath());
                        }

                        mFileLists.addAll(apkFiles);
                        CollectionUtils.shortingByFirstCase(mFileLists);
                        updateTitle(Const.PAGE_APP_TITLE);
                        notifyRvDataChanged();
                        ApkUtils.asyncCacheApkIco(mContext, apkFiles);
                    });


                } else {
                    observable.subscribe((Consumer<List<FileInfo>>) fileInfoList -> {
                        if (fileInfoList.size() == 0) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "没有扫描到文件", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mFileLists.addAll(fileInfoList);
                        CollectionUtils.shortingByFirstCase(mFileLists);
                        updateTitle(Const.PAGE_MUSIC_TITLE);
                        notifyRvDataChanged();
                        MusicUtils.updateAlbumImg(getContext(), mFileLists);
                    });
                }


            }
        };

    }

    @Override
    public void onDestroyView() {
        mFileLoadManager.destroyLoader();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mFileLoadManager.destroyLoader();
        super.onDestroy();
    }

    private void initRecyclerView() {
        switch (mLoadFileType) {
            case FILE_TYPE_APP:
                updateTitle("应用");
                mRvFileList.setLayoutManager(new GridLayoutManager(getContext(), 4));
                mFileListAdapter = new FileAdapter<>(getActivity(), R.layout.item_rv_apk, mFileLists);
                break;

            case FILE_TYPE_MUSIC:
                updateTitle("音乐");
                mRvFileList.setLayoutManager(new LinearLayoutManager(getContext()));
                mFileListAdapter = new FileAdapter<>(getActivity(), R.layout.item_rv_music, mFileLists);
                break;

            case FILE_TYPE_VIDEO:
                updateTitle("视频");
                mRvFileList.setLayoutManager(new LinearLayoutManager(getContext()));
                mFileListAdapter = new FileAdapter<>(getActivity(), R.layout.item_rv_video, mFileLists);
                break;

            default:
                break;
        }
        // 设置空布局， 需要将布局文件转换成View后才能设置，否则会报错
        mFileListAdapter.setEmptyView(View.inflate(mContext, R.layout.view_rv_file_empty, null));
        // 设置适配器
        mRvFileList.setAdapter(mFileListAdapter);
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
        mTvTitle.setText(type + "(" + mFileLists.size() + ")");
    }

    private boolean isSelectedAllFile() {
        for (FileInfo fileInfo : mFileLists) {
            if (!App.getSendFileList().contains(fileInfo)) {
                return false;
            }
        }
        return true;
    }
}
