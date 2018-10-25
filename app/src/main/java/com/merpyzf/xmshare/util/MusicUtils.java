package com.merpyzf.xmshare.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.util.FileUtils;
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

import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_MUSIC;

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
    public static synchronized void writeAlbumImgList2local(final Context context, List<FileInfo> musicList) {

        Observable.fromIterable(musicList)
                .flatMap(musicFile -> Observable.just(((MusicFile) musicFile))).subscribeOn(Schedulers.io())
                .subscribe(musicFile -> writeAlbumImg2local(context, musicFile));
    }

    @SuppressLint("CheckResult")
    public static synchronized void writeAlbumImg2local(final Context context, MusicFile musicFile) {
        Bitmap bitmap = loadCoverFromMediaStore(context, musicFile.getAlbumId());
        BufferedOutputStream bos = null;
        try {
            if (bitmap == null) {
                musicFile.setAlbumId(-1);
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_album_art);
            }
            File albumFile = new File(FilePathManager.getMusicAlbumCacheDir(), musicFile.getAlbumId() + ".png");
            if (FilePathManager.getMusicAlbumCacheDir().canWrite() && !isContain(FilePathManager.getMusicAlbumCacheDir(), musicFile)) {
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
     * 判断要获取到音乐封面是否在本地已经存在
     *
     * @param musicFile
     * @return true
     * false 不包含
     */
    private static synchronized boolean isContain(File parent, MusicFile musicFile) {
        String[] albums = parent.list();
        for (int i = 0; i < albums.length; i++) {
            if ((musicFile.getAlbumId() + ".png").equals(albums[i])) {
                Log.i("WW2K", "专辑封面已经存在了");
                return true;
            }
        }
        // TODO: 2017/12/24 考虑增加清理音乐文件不存在的album_id
        return false;
    }

    /**
     * 更新封面图片
     */
    public static synchronized void updateAlbumImg(Context context, List<FileInfo> fileInfoList) {
        writeAlbumImgList2local(context, fileInfoList);
    }

    public static Observable<List<FileInfo>> asyncLoadingMusic(Cursor data) {
        return Observable.just(data)
                .flatMap(cursor -> {
                    List<FileInfo> fileList = new ArrayList<>();
                    data.moveToFirst();
                    // 遍历扫描到的音乐文件
                    while (data.moveToNext()) {
                        String title = data.getString(0);
                        String artist = data.getString(1);
                        String path = data.getString(2);
                        int albumId = data.getInt(3);
                        // 注意这边的音乐文件的大小是否正确
                        long extraMaxBytes = data.getLong(4);
                        long duration = data.getLong(5);
                        MusicFile fileInfo = new MusicFile(title, path, FILE_TYPE_MUSIC,
                                (int) extraMaxBytes, albumId, artist, duration);
                        // 添加文件的后缀名
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
