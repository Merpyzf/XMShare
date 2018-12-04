package com.merpyzf.xmshare.util;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.xmshare.bean.PhotoDirBean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * description: 照片操作相关的工具类
 * date: 2018/8/18.
 * version: 1.0.0
 * @author wangke
 */
public class PhotoUtils {

    private PhotoUtils() {
        throw new Error("Do not need instantiate!");
    }

    public static io.reactivex.Observable<List<PhotoDirBean>> AsyncLoadingFromCourse(Cursor data) {

        io.reactivex.Observable<List<PhotoDirBean>> observable = Observable.just(data)
                .map(cursor -> {
                    List<PhotoDirBean> mDirList = new ArrayList<>();
                    Set<String> dirSet;
                    if (cursor.getCount() > 0) {
                        dirSet = new HashSet<>();
                        cursor.moveToFirst();
                        while (cursor.moveToNext()) {
                            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                            File file = new File(path);
                            if (!dirSet.contains(file.getParent())) {
                                File[] images = file.getParentFile().listFiles(PhotoUtils::checkPhoto);
                                if (null != images && images.length > 0) {
                                    PhotoDirBean photoDirBean = new PhotoDirBean();
                                    photoDirBean.setCoverImg(images[images.length - 1].getPath());
                                    photoDirBean.setName(file.getParentFile().getName());
                                    photoDirBean.setImageList(images);
                                    Md5Utils.asyncGenerateFileMd5(photoDirBean.getImageList());
                                    mDirList.add(photoDirBean);
                                }
                                dirSet.add(file.getParent());
                            }
                            // TODO: 2018/11/11 偶尔会出现cursor关闭的问题
                            if (data.isClosed()) {
                                break;
                            }
                        }

                    }
                    return mDirList;
                })
                // 设置被观察者所在的线程
                .subscribeOn(Schedulers.io())
                // 设置观察者所在的线程
                .observeOn(AndroidSchedulers.mainThread());

        return observable;

    }

    public static boolean checkPhoto(File photoFile) {
        String fileSuffix = FileUtils.getFileSuffix(photoFile.getPath()).toLowerCase();
        if (!"".equals(fileSuffix) && photoFile.length() > 20 * 1024) {
            if ("jpg".equals(fileSuffix) || "jpeg".equals(fileSuffix)
                    || "gif".equals(fileSuffix) || "png".equals(fileSuffix)
                    || "bpm".equals(fileSuffix) || "webp".equals(fileSuffix)) {
                return true;
            } else {
                return false;
            }
        } else {

            return false;
        }
    }

    /**
     * 统计文件夹中总的文件个数
     *
     * @param mPhotoDirs 存放图片的父路径集合
     */
    public static Observable<Integer> getNumberOfPhotos(List<PhotoDirBean> mPhotoDirs) {
        Observable<Integer> observable = Observable.just(mPhotoDirs)
                .map(photoDirBeans -> {
                    int photoCount = 0;
                    for (PhotoDirBean photoDirBean : photoDirBeans) {
                        int size = photoDirBean.getImageList().size();
                        photoCount += size;
                    }
                    return photoCount;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return observable;
    }
}
