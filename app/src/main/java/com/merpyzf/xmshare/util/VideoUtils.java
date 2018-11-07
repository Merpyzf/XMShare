package com.merpyzf.xmshare.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.util.Log;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.VideoFile;
import com.merpyzf.transfermanager.util.CloseUtils;
import com.merpyzf.transfermanager.util.FilePathManager;
import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.xmshare.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.merpyzf.transfermanager.entity.FileInfo.FILE_TYPE_VIDEO;

/**
 * Created by wangke on 2018/1/14.
 */

public class VideoUtils {

    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bmp = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bmp = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();

        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bmp;
    }


    /**
     * 更新封面图片
     */
    public static void updateThumbImg(Context context, List<FileInfo> fileInfoList) {

        List<FileInfo> copyFileInfoList = new ArrayList<>();
        copyFileInfoList.addAll(fileInfoList);
        writeThumbImg2local(context, copyFileInfoList);

    }


    @SuppressLint("CheckResult")
    public static void writeThumbImg2local(Context context, List<FileInfo> videoList) {


        Observable.fromIterable(videoList)
                .filter(videoFile -> {
                    if (videoFile instanceof VideoFile) {
                        if (FilePathManager.getLocalVideoThumbCacheDir().canWrite() && !isContain(FilePathManager.getLocalVideoThumbCacheDir(), (VideoFile) videoFile)) {
                            return true;
                        }
                    }
                    return false;
                }).flatMap(videoFile -> Observable.just(videoFile.getPath()))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(videoPath -> {
                    Bitmap bitmap = getVideoThumbnail(videoPath);
                    BufferedOutputStream bos = null;
                    String fileName = new File(videoPath).getName();
                    int dotIndex = fileName.lastIndexOf('.');
                    fileName = fileName.substring(0, dotIndex);
                    try {
                        File videoThumb = FilePathManager.getLocalVideoThumbCacheFile(fileName);
                        boolean isContain = FileUtils.isContain(videoThumb.getParentFile(), videoThumb.getName());
                        if(!isContain) {
                            bos = new BufferedOutputStream(new FileOutputStream(videoThumb));
                            if (bitmap == null) {
                                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_video);
                            }
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        CloseUtils.close(bos);
                    }
                });
    }

    private static synchronized boolean isContain(File parent, VideoFile videoFile) {
        String[] thumbs = parent.list();

        for (int i = 0; i < thumbs.length; i++) {
            if (Md5Utils.getMd5(videoFile.getPath()).equals(thumbs[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 异步加载视频
     *
     * @param data Observable<List<FileInfo>>
     */
    public static Observable<List<FileInfo>> asyncLoadingVideo(Cursor data) {
        return Observable.just(data)
                .flatMap(cursor -> {
                    List<FileInfo> fileList = new ArrayList<>();
                    data.moveToFirst();
                    while (data.moveToNext()) {
                        VideoFile videoFile = new VideoFile();
                        int length = (int) data.getLong(data.getColumnIndex(MediaStore.Video.Media.SIZE));
                        String path = data.getString(data.getColumnIndex(MediaStore.Video.Media.DATA));
                        videoFile.setAlbumId(data.getString(data.getColumnIndex(MediaStore.Video.Media._ID)));
                        videoFile.setName(data.getString(data.getColumnIndex(MediaStore.Video.Media.TITLE)));
                        videoFile.setPath(path);
                        videoFile.setLength(length);
                        videoFile.setDuration(data.getLong(data.getColumnIndex(MediaStore.Video.Media.DURATION)));
                        // 设置文件后缀
                        videoFile.setSuffix(FileUtils.getFileSuffix(path));
                        Log.i("WK", "视频文件: " + videoFile.getName() + " 后缀: " + videoFile.getSuffix());

                        // 设置文件类型
                        videoFile.setType(FILE_TYPE_VIDEO);
                        // 筛选大于1MB的文件
                        if (length > 1024 * 1024) {
                            fileList.add(videoFile);
                            Log.i("wk", "将视频文件添加到FileList");
                        }
                    }
                    return Observable.just(fileList);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
