package com.merpyzf.xmshare.ui.activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.entity.StorageFile;
import com.merpyzf.transfermanager.entity.VideoFile;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.db.AppDatabase;
import com.merpyzf.xmshare.db.entity.FileCache;
import com.merpyzf.xmshare.ui.adapter.SearchAdapter;
import com.merpyzf.xmshare.ui.widget.RecyclerViewDivider;
import com.merpyzf.xmshare.util.FileTypeHelper;
import com.merpyzf.xmshare.util.ToastUtils;

import net.qiujuer.genius.res.Resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 用于进行文件搜索的Activity
 *
 * @author wangke
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener, BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.edt_search)
    EditText mEdtSearch;
    @BindView(R.id.rv_filelist)
    RecyclerView mRvFileList;
    private volatile List<BaseFileInfo> mSearchFiles = new ArrayList<>();
    private SearchAdapter mSearchAdapter;

    @Override
    protected int getContentLayoutId() {
        initWindowTransition();
        return R.layout.activity_search;
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {
        mEdtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mEdtSearch.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        mRvFileList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvFileList.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.VERTICAL));
        mSearchAdapter = new SearchAdapter(R.layout.item_rv_search, mSearchFiles);
        mRvFileList.setAdapter(mSearchAdapter);
        mSwipeRefresh.setEnabled(false);
        mSwipeRefresh.setColorSchemeColors(Resource.Color.ORANGE, Resource.Color.BLUE, Resource.Color.GREY);
    }

    @Override
    protected void initEvents() {
        mIvBack.setOnClickListener(this);
        mEdtSearch.setOnEditorActionListener(this);
        mSearchAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            default:
                break;
        }

    }

    /**
     * 初始化窗口进入，退出的动画效果
     */
    private void initWindowTransition() {
        getWindow().setEnterTransition(new Fade().setDuration(200));
        getWindow().setExitTransition(new Fade().setDuration(200));
    }

    /**
     * 搜索
     */
    @SuppressLint("CheckResult")
    private void search(String searchContent) {
        mSwipeRefresh.setRefreshing(true);
        clearLastSearchResults();
        Observable<List<BaseFileInfo>> searchObservable = searchMultipleTypeFile(searchContent);
        searchObservable.subscribe(files -> {
            if (files.size() == 0) {
                ToastUtils.showShort(mContext, "主人,没能找到您要查找的文件!");
                mSwipeRefresh.setRefreshing(false);
                return;
            }
            if (mSearchFiles.size() != 0) {
                mSearchFiles.clear();
            }
            mSearchFiles.addAll(files);
            mSearchAdapter.notifyDataSetChanged();
            mSwipeRefresh.setRefreshing(false);
        });
    }

    private void clearLastSearchResults() {
        if (mSearchFiles.size() != 0) {
            mSearchFiles.clear();
            mSearchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_UP:
                    String searchContent = mEdtSearch.getText().toString();
                    if (TextUtils.isEmpty(searchContent)) {
                        ToastUtils.showShort(mContext, "请输入要查找的文件名称！");

                    } else {
                        search(searchContent);
                    }
                    break;
                default:
                    return true;
            }
            return true;
        }
        return false;
    }


    @SuppressLint("CheckResult")
    private Observable<List<BaseFileInfo>> searchMultipleTypeFile(String key) {
        String selection = buildSelectionByArgs(Const.MIME_TYPES);
        selection += "and (" + MediaStore.Files.FileColumns.TITLE + " like " + "\"%" + key + "%\" )";
        List<BaseFileInfo> searchFiles = new ArrayList<>();
        Observable<List<BaseFileInfo>> searchObservable = Observable.just(selection)
                .flatMap((Function<String, ObservableSource<List<BaseFileInfo>>>) selection1 -> {
                    // 搜索应用
                    List<ApkFile> apps = searchApp(key);
                    searchFiles.addAll(apps);
                    ContentResolver mContentResolver = mContext.getContentResolver();
                    Cursor cursor = mContentResolver.query(MediaStore.Files.getContentUri("external"), null, selection1, Const.MIME_TYPES, null);
                    while (cursor.moveToNext()) {
                        String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
                        if (mimeType.startsWith("image")) {
                            PicFile picFile = new PicFile();
                            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                            long length = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));

                            picFile.setPath(path);
                            picFile.setName(title);
                            picFile.setLength(length);
                            picFile.setType(BaseFileInfo.FILE_TYPE_IMAGE);
                            searchFiles.add(picFile);
                        } else if (mimeType.startsWith("audio")) {
                            MusicFile musicFile = new MusicFile();
                            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                            long length = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                            int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                            musicFile.setName(title);
                            musicFile.setArtist(artist);
                            musicFile.setAlbumId(albumId);
                            musicFile.setPath(path);
                            musicFile.setLength(length);
                            musicFile.setDuration(duration);
                            musicFile.setType(BaseFileInfo.FILE_TYPE_MUSIC);
                            searchFiles.add(musicFile);
                        } else if (mimeType.startsWith("video")) {
                            VideoFile videoFile = new VideoFile();

                            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                            String albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                            long length = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                            videoFile.setName(title);
                            videoFile.setPath(path);
                            videoFile.setAlbumId(albumId);
                            videoFile.setLength(length);
                            videoFile.setDuration(duration);
                            videoFile.setType(BaseFileInfo.FILE_TYPE_VIDEO);
                            searchFiles.add(videoFile);
                        } else {
                            StorageFile storageFile = new StorageFile();
                            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));

                            storageFile.setName(title);
                            storageFile.setPath(path);
                            File file = new File(path);
                            storageFile.setLength(file.length());
                            storageFile.setPhoto(FileTypeHelper.isPhotoType(storageFile.getSuffix()));
                            storageFile.setType(BaseFileInfo.FILE_TYPE_STORAGE);
                        }
                    }
                    return Observable.just(searchFiles);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return searchObservable;
    }

    private String buildSelectionByArgs(String[] selectionArgs) {
        StringBuffer selection = new StringBuffer();
        selection.append("(");
        for (int i = 0; i < selectionArgs.length; i++) {
            if (i != selectionArgs.length - 1) {
                selection.append(MediaStore.Files.FileColumns.MIME_TYPE + "= ? " + " or ");
            } else {
                selection.append(MediaStore.Files.FileColumns.MIME_TYPE + "= ?");
            }
        }
        selection.append(")");
        return selection.toString();
    }

    private List<BaseFileInfo> getAllDocument() {

        String[] projection = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.MIME_TYPE};
        // 查询语句的条件
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "= ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ";

        // 查询语句的参数值
        String[] selectionArgs = new String[]{
                //doc
                "application/msword",
                //pdf
                "application/pdf",
                //powerpoint
                "application/vnd.ms-powerpoint",
                //文本
                "text/plain",
                //excel
                "application/vnd.ms-excel",
        };

        ContentResolver mContentResolver = mContext.getContentResolver();
        // 第二个参数为要查询的属性
        // 第三个参数为查询条件
        //
        Cursor cursor = mContentResolver.query(MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, null);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            Log.i("WW2k", "data: " + data);
        }

        return null;


    }

    @SuppressLint("CheckResult")
    private List<ApkFile> searchApp(String queryKey) {
        final List<ApkFile> appList = new ArrayList<>();
        List<FileCache> apps = AppDatabase
                .getInstance(mContext)
                .getFileCacheDao()
                .queryLike(queryKey);
        for (FileCache app : apps) {
            ApkFile appFile = new ApkFile();
            appFile.setName(app.getName());
            appFile.setPath(app.getPath());
            appFile.setLength(new File(app.getPath()).length());
            appFile.setType(BaseFileInfo.FILE_TYPE_APP);
            appList.add(appFile);
        }
        return appList;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {




    }
}
