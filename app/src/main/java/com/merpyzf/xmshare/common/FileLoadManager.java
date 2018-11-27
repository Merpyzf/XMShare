package com.merpyzf.xmshare.common;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.xmshare.bean.PhotoDirBean;
import com.merpyzf.xmshare.util.ApkUtils;
import com.merpyzf.xmshare.util.MusicUtils;
import com.merpyzf.xmshare.util.PhotoUtils;
import com.merpyzf.xmshare.util.VideoUtils;

import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;

import static com.merpyzf.transfermanager.entity.BaseFileInfo.FILE_TYPE_APP;
import static com.merpyzf.transfermanager.entity.BaseFileInfo.FILE_TYPE_IMAGE;
import static com.merpyzf.transfermanager.entity.BaseFileInfo.FILE_TYPE_MUSIC;
import static com.merpyzf.transfermanager.entity.BaseFileInfo.FILE_TYPE_VIDEO;

/**
 * 文件加载管理器，加载设备内文件
 */
public abstract class FileLoadManager implements LoaderManager.LoaderCallbacks<Cursor> {
    private Activity mContext;
    private int mLoadFileType;
    private LoaderManager mLoaderManager;

    public FileLoadManager(Activity activity, int loadFileType) {
        this.mContext = activity;
        this.mLoadFileType = loadFileType;
        initLoadManager();
        if (mLoadFileType == FILE_TYPE_APP) {
            loadApp();
        }
    }

    private void initLoadManager() {
        mLoaderManager = Objects.requireNonNull(mContext).getLoaderManager();
        mLoaderManager.initLoader(mLoadFileType, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        String[] projections;
        // 扫描音乐文件
        if (id == FILE_TYPE_MUSIC) {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            projections = new String[]{
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.SIZE,
                    MediaStore.Audio.Media.DURATION
            };
            return new CursorLoader(mContext, uri, projections, null, null, MediaStore.Audio.Media.DATE_ADDED + " DESC");

        } else if (id == FILE_TYPE_IMAGE) {
            if (id == BaseFileInfo.FILE_TYPE_IMAGE) {
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                projections = new String[]
                        {
                                MediaStore.Images.Media.DATA,
                        };
                return new CursorLoader(mContext, uri, projections, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
            }

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
            return new CursorLoader(mContext, uri, projections, null, null, MediaStore.Video.Media.DATE_ADDED + " DESC");
        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mLoadFileType == FILE_TYPE_MUSIC) {
            Observable<List<BaseFileInfo>> observable = MusicUtils.asyncLoadingMusic(data);
            onLoadFinished(observable);
        } else if (mLoadFileType == FILE_TYPE_VIDEO) {
            Observable<List<BaseFileInfo>> observable = VideoUtils.asyncLoadingVideo(data);
            onLoadFinished(observable);
        } else if (mLoadFileType == FILE_TYPE_IMAGE) {
            Observable<List<PhotoDirBean>> observable = PhotoUtils.AsyncLoadingFromCourse(data);
            onLoadFinished(observable);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void loadApp() {
        Observable observable = ApkUtils.asyncLoadApp(mContext);
        onLoadFinished(observable);
    }

    public abstract void onLoadFinished(Observable observable);

    /**
     * 需要及时销毁LoaderManager
     */
    public void destroyLoader() {
        mLoaderManager.destroyLoader(this.mLoadFileType);
    }


}
