package com.merpyzf.xmshare.common;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.xmshare.bean.PhotoDirBean;
import com.merpyzf.xmshare.ui.fragment.FileListFragment;
import com.merpyzf.xmshare.util.ApkUtils;
import com.merpyzf.xmshare.util.CollectionUtils;
import com.merpyzf.xmshare.util.MusicUtils;
import com.merpyzf.xmshare.util.PhotoUtils;
import com.merpyzf.xmshare.util.VideoUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.RxFragment;

import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_APP;
import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_IMAGE;
import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_MUSIC;
import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_VIDEO;
import static org.litepal.crud.DataSupport.markAsDeleted;

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
            return new CursorLoader(mContext, uri, projections, null, null, MediaStore.Audio.Media.DATE_ADDED + " DESC");

        } else if (id == FILE_TYPE_IMAGE) {
            if (id == FileInfo.FILE_TYPE_IMAGE) {
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
            Log.i("WW2k", "Music cursor-->"+data.hashCode());
            Observable<List<FileInfo>> observable = MusicUtils.asyncLoadingMusic(data);
            onLoadFinished(observable);
        } else if (mLoadFileType == FILE_TYPE_VIDEO) {
            Log.i("WW2k", "VIDEO cursor-->"+data.hashCode());
            Observable<List<FileInfo>> observable = VideoUtils.asyncLoadingVideo(data);
            onLoadFinished(observable);
        } else if (mLoadFileType == FILE_TYPE_IMAGE) {
            Log.i("WW2k", "IMAGE cursor-->"+data.hashCode());
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
