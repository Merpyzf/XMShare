package com.merpyzf.xmshare.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.common.utils.FilePathManager;
import com.merpyzf.transfermanager.utils.FileUtils;
import com.merpyzf.xmshare.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.merpyzf.transfermanager.entity.BaseFileInfo.FILE_TYPE_MUSIC;

/**
 * Created by wangke on 2017/12/23.
 */

public class MusicUtils {
    /**
     * 从媒体库加载封面
     */
    public static Bitmap loadCoverFromMediaStore(Context context, long albumId) {

        ContentResolver resolver = context.getContentResolver();
        Uri uri = getMediaStoreAlbumCoverUri(albumId);
        InputStream is;
        try {
            is = resolver.openInputStream(uri);
        } catch (Exception ignored) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeStream(is, null, options);
    }

    /**
     * 获取音乐封面图的uri
     *
     * @param albumId
     * @return
     */
    public static Uri getMediaStoreAlbumCoverUri(long albumId) {
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, albumId);
    }

    /**
     * 将专辑封面图缓存到本地
     *
     * @param context
     * @param musicList
     */
    @SuppressLint("CheckResult")
    public static void asyncWriteAlbumListToLocal(final Context context, List<BaseFileInfo> musicList) {
        Observable.fromIterable(musicList)
                .flatMap(musicFile -> Observable.just(((MusicFile) musicFile))).subscribeOn(Schedulers.io())
                .subscribe(musicFile -> writeAlbumToLocal(context, musicFile));
    }

    @SuppressLint("CheckResult")
    public static void writeAlbumToLocal(final Context context, MusicFile musicFile) {
        Bitmap bitmap = loadCoverFromMediaStore(context, musicFile.getAlbumId());
        BufferedOutputStream bos = null;
        try {
            if (bitmap == null) {
                musicFile.setAlbumId(-1);
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_holder_album_art);
            }
            File albumFile = FilePathManager.getLocalMusicAlbumCacheFile(String.valueOf(musicFile.getAlbumId()));
            if (FilePathManager.getLocalMusicAlbumCacheDir().canWrite() &&
                    !FileUtils.isContain(albumFile.getParentFile(), albumFile.getName())) {
                bos = new BufferedOutputStream(new FileOutputStream(albumFile));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新封面图片
     */
    public static void asyncUpdateAlbumImg(Context context, List<BaseFileInfo> fileInfoList) {
        asyncWriteAlbumListToLocal(context, fileInfoList);
    }

    public static Observable<List<BaseFileInfo>> asyncLoadingMusic(Cursor data) {
        return Observable.just(data)
                .flatMap(cursor -> {
                    List<BaseFileInfo> fileList = new ArrayList<>();
                    data.moveToFirst();
                    while (data.moveToNext()) {
                        String title = data.getString(0);
                        String artist = data.getString(1);
                        String path = data.getString(2);
                        int albumId = data.getInt(3);
                        long extraMaxBytes = data.getLong(4);
                        long duration = data.getLong(5);
                        MusicFile fileInfo = new MusicFile(title, path, FILE_TYPE_MUSIC,
                                (int) extraMaxBytes, albumId, artist, duration);
                        fileInfo.setSuffix(FileUtils.getFileSuffix(path));
                        if (extraMaxBytes > 1024 * 1024) {
                            fileList.add(fileInfo);
                        }
                    }
                    return Observable.just(fileList);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
