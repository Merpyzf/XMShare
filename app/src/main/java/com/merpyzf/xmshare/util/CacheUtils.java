package com.merpyzf.xmshare.util;

import android.annotation.SuppressLint;
import android.content.Context;

import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.xmshare.db.AppDatabase;
import com.merpyzf.xmshare.db.dao.FileCacheDao;
import com.merpyzf.xmshare.db.entity.FileCache;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 2018/11/27
 *
 * @author wangke
 */
public class CacheUtils {

    private CacheUtils() {

    }

    @SuppressLint("CheckResult")
    public static void cacheAppInfo(Context context, List<ApkFile> appList) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        FileCacheDao fileCacheDao = appDatabase.getFileCacheDao();
        // 遍历
        Observable.fromIterable(appList)
                .filter(apkFile -> {
                    FileCache fileCache = fileCacheDao.queryByPath(apkFile.getPath());
                    return fileCache == null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(apkFile -> {
                    FileCache fc = new FileCache(apkFile.getName(), apkFile.getPath(), BaseFileInfo.FILE_TYPE_APP);
                    fileCacheDao.insert(fc);
                });
    }


}
