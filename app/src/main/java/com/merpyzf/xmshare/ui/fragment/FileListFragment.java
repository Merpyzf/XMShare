package com.merpyzf.xmshare.ui.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
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
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.receiver.FileSelectedListChangedReceiver;
import com.merpyzf.xmshare.ui.adapter.FileAdapter;
import com.merpyzf.xmshare.ui.activity.OnFileSelectListener;
import com.merpyzf.xmshare.ui.activity.SearchActivity;
import com.merpyzf.xmshare.ui.activity.SelectFilesActivity;
import com.merpyzf.xmshare.util.AnimationUtils;
import com.merpyzf.xmshare.util.ApkUtils;
import com.merpyzf.xmshare.util.Md5Utils;
import com.merpyzf.xmshare.util.MusicUtils;
import com.merpyzf.xmshare.util.VideoUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_APP;
import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_IMAGE;
import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_MUSIC;
import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_VIDEO;

/**
 * 扫描到的本地文件列表的展示页面
 * 扫描: 应用、音乐、视频
 *
 * @author wangke
 */
public class FileListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

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

    private LoaderManager mLoaderManager;
    private int loadFileType = 1;
    private List<FileInfo> mFileLists = new ArrayList<>();
    private FileAdapter mFileListAdapter;
    private OnFileSelectListener<FileInfo> mFileSelectListener;
    private FileSelectedListChangedReceiver mFslcReceiver;
    private View bottomSheetView;
    private static final String TAG = FileListFragment.class.getSimpleName();
    private ImageView mIvSearch;

    @SuppressLint("ValidFragment")
    public FileListFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    private FileListFragment(int type, OnFileSelectListener<FileInfo> fileSelectListener) {
        super();
        this.loadFileType = type;
        this.mFileSelectListener = fileSelectListener;
    }

    public static FileListFragment newInstance(int type, OnFileSelectListener<FileInfo> fileSelectListener) {
        return new FileListFragment(type, fileSelectListener);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_file_list;
    }

    @Override
    protected void initData() {
        super.initData();
        initLoaderManager();
        // todo 思考下面的这个清空文件列表的方式是否应该存在
        if (App.getSendFileList().size() > 0) {
            App.getSendFileList().clear();
        }
    }


    @Override
    protected void initWidget(View rootView) {
        mIvSearch = getActivity().findViewById(R.id.iv_action_search);
        bottomSheetView = getActivity().findViewById(R.id.bottom_sheet);
        if (mCheckBoxAll.isChecked()) {
            mTvChecked.setText("取消全选");
        } else {
            mTvChecked.setText("全选");
        }
        switch (loadFileType) {
            case FILE_TYPE_APP:
                updateTitle("应用");
                mRvFileList.setLayoutManager(new GridLayoutManager(getContext(), 4));
                mFileListAdapter = new FileAdapter<>(getActivity(), R.layout.item_rv_apk, mFileLists);
                asyncLoadApp();
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
                if (mFileSelectListener != null) {
                    mFileSelectListener.onSelected(fileInfo);
                }
                View startView = view.findViewById(R.id.iv_cover);
                View targetView = null;
                if (getActivity() != null && (getActivity() instanceof SelectFilesActivity)) {
                    targetView = bottomSheetView;
                }
                AnimationUtils.setAddTaskAnimation(getActivity(), startView, targetView, null);
            } else {
                ivSelect.setVisibility(View.INVISIBLE);
                App.removeSendFile(fileInfo);
                if (mFileSelectListener != null) {
                    mFileSelectListener.onCancelSelected(fileInfo);
                }
            }
            mCheckBoxAll.setChecked(isSelectedAllFile());
        });
        // 当选择的文件列表发生改变时的回调
        mFslcReceiver = new FileSelectedListChangedReceiver() {
            @Override
            public void onFileListChanged(String unSelectedFile) {
                // 当选择的文件列表发生改变时的回调
                mFileListAdapter.notifyDataSetChanged();
                mCheckBoxAll.setChecked(isSelectedAllFile());
            }
        };
        mCheckBoxAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 全选
            if (isChecked) {
                mTvChecked.setText("取消全选");
                if (mFileSelectListener != null) {
                    mFileSelectListener.onSelectedAll(mFileLists);
                }

                mFileListAdapter.notifyDataSetChanged();
                mTvChecked.setText("取消全选");
            } else {
                if (isSelectedAllFile()) {
                    if (mFileSelectListener != null) {
                        mFileSelectListener.onCancelSelectedAll(mFileLists);
                    }
                }
                mFileListAdapter.notifyDataSetChanged();
                mTvChecked.setText("全选");
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        registerFileListChangedReceiver();
    }

    /**
     * 判断本页面所展示的文件是否被全部选中
     *
     * @return true : 全选 false : 未全选
     */
    private boolean isSelectedAllFile() {
        for (FileInfo fileInfo : mFileLists) {
            if (!App.getSendFileList().contains(fileInfo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 异步加载本地的app应用
     */
    @SuppressLint("CheckResult")
    private void asyncLoadApp() {
        if (loadFileType == FILE_TYPE_APP) {
            ApkUtils.asyncLoadApp(getActivity())
                    .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribe(apkFiles -> {
                        mFileLists.addAll(apkFiles);
                        shortingByFirstCase(mFileLists);
                        updateTitle(Const.PAGE_APP_TITLE);
                        updateRvFileList();
                        ApkUtils.asyncCacheApkIco(mContext, apkFiles);
                    });
        }
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri;
        String[] projections;
        // 扫描音乐文件
        if (id == FILE_TYPE_MUSIC) {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            projections = new String[]{
                    //音乐名
                    MediaStore.Audio.Media.TITLE,
                    // 艺术家
                    MediaStore.Audio.Media.ARTIST,
                    //音乐文件所在路径
                    MediaStore.Audio.Media.DATA,
                    // 音乐封面Id
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.SIZE,
                    //音乐时长
                    MediaStore.Audio.Media.DURATION
            };
            return new CursorLoader(getContext(), uri, projections, null, null, MediaStore.Audio.Media.DATE_ADDED + " DESC");
            // 扫描图片文件
        } else if (id == FILE_TYPE_IMAGE) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projections = new String[]
                    {
                            MediaStore.Images.Media._ID,
                            MediaStore.Images.Media.DATA,
                            MediaStore.Video.Media.TITLE,
                            // 文件添加/修改时间
                            MediaStore.Images.Media.DATE_ADDED
                    };
            return new CursorLoader(getContext(), uri, projections, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
            // 扫描视频文件
        } else if (id == FILE_TYPE_VIDEO) {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projections = new String[]
                    {
                            MediaStore.Video.Media._ID,
                            MediaStore.Video.Media.TITLE,
                            MediaStore.Video.Media.DURATION,
                            MediaStore.Video.Media.DATA,
                            MediaStore.Video.Media.SIZE,
                            MediaStore.Video.Media.DATE_ADDED
                    };
            return new CursorLoader(getContext(), uri, projections, null, null, MediaStore.Video.Media.DATE_ADDED + " DESC");
        }
        return null;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if (loadFileType == FILE_TYPE_MUSIC) {
            MusicUtils.asyncLoadingMusic(data)
                    .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribe(fileInfoList -> {
                        if (fileInfoList.size() == 0) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "没有扫描到本地音乐", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mFileLists.addAll(fileInfoList);
                        shortingByFirstCase(mFileLists);
                        updateTitle(Const.PAGE_MUSIC_TITLE);
                        updateRvFileList();
                        MusicUtils.updateAlbumImg(getContext(), mFileLists);
                        // 异步生成并文件的MD5并写入到数据库中
                        //Md5Utils.asyncGenerateFileMd5(mFileLists);
                    });

        } else if (loadFileType == FILE_TYPE_VIDEO) {
            VideoUtils.asyncLoadingVideo(data)
                    .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribe(fileInfoList -> {
                        if (fileInfoList.size() == 0) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(mContext, "没有在当前设备上扫描到视频", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mFileLists.addAll(fileInfoList);
                        shortingByFirstCase(mFileLists);
                        updateTitle(Const.PAGE_VIDEO_TITLE);
                        updateRvFileList();
                        VideoUtils.updateThumbImg(mContext, mFileLists);
                        //Md5Utils.asyncGenerateFileMd5(mFileLists);
                    });
        }
    }

    private void shortingByFirstCase(List<FileInfo> fileLists) {
        Collections.sort(fileLists, (o1, o2) -> {
            Integer integer1 = (int) o1.getFirstCase();
            Integer integer2 = (int) o2.getFirstCase();
            return integer1.compareTo(integer2);
        });
    }


    /**
     * 更新显示RecyclerView的内容
     */
    private void updateRvFileList() {
        mRvFileList.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mFileListAdapter.notifyDataSetChanged();

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
    public void onLoaderReset(Loader loader) {


    }


    public void updateTitle(String type) {
        mTvTitle.setText(type + "(" + mFileLists.size() + ")");
    }

    /**
     * 初始化LoaderManager
     */
    private void initLoaderManager() {
        mLoaderManager = Objects.requireNonNull(getActivity()).getLoaderManager();
        mLoaderManager.initLoader(loadFileType, null, FileListFragment.this);
    }

    public List<FileInfo> getFileLists() {
        return mFileLists;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            mIvSearch.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileList", (Serializable) mFileLists);
                intent.putExtras(bundle);
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
        mLoaderManager.destroyLoader(loadFileType);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mLoaderManager.destroyLoader(loadFileType);
        super.onDestroy();
    }
}
