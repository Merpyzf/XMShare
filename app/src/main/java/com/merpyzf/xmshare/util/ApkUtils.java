package com.merpyzf.xmshare.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.util.CloseUtils;
import com.merpyzf.transfermanager.util.FilePathManager;
import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.xmshare.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangke on 2017/12/25.
 */

public class ApkUtils {


    /**
     * 获取本地已安装的第三方APK的信息
     *
     * @param mPackageManager
     * @return
     */
    public static List<ApkFile> getApp(Activity context, PackageManager mPackageManager) {
        long start = System.currentTimeMillis();
        List<ApkFile> apkFileList = new ArrayList<>();
        List<ApplicationInfo> appList = mPackageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
            //获取当前设备中的第三方的app
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                try {
                    ApkFile app = new ApkFile();
                    //获取app的name
                    String appName = (String) mPackageManager.getApplicationLabel(appInfo);
                    //获取app的图标
                    Drawable appIco = mPackageManager.getApplicationIcon(appInfo);
                    String appSourcePath = context.getApplication().getPackageManager().getApplicationInfo(appInfo.packageName, 0).sourceDir;
                    File file = new File(appSourcePath);
                    long length = file.length();
                    app.setApkDrawable(appIco);
                    app.setName(appName);
                    app.setPath(appSourcePath);
                    app.setLength((int) length);
                    app.setSuffix("apk");
                    app.setType(FileInfo.FILE_TYPE_APP);
                    apkFileList.add(app);
                    //Log.i("WK", "应用: " + app.getName() + " 后缀: " + app.getSuffix());

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        long end = System.currentTimeMillis();
        Log.i("wk", "扫描本地安装应用总共耗时:" + (end - start) / 1000f);
        return apkFileList;
    }


    /**
     * 获取Drawable实际占用大小
     *
     * @param drawable
     * @return
     */
    public static int getDrawableSize(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int len = baos.toByteArray().length;
        return len;
    }


    /**
     * 通过包名获取路径/data/app/ **.apk
     *
     * @param packageName
     * @return
     */
    public static String getApkFilePath(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 获取apk的图标信息
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                Log.e("ApkIconLoader", e.toString());
            }
        }
        return null;
    }

    /**
     * 缓存apk的ico
     */
    @SuppressLint("CheckResult")
    public static void asyncCacheApkIco(Context context, List<ApkFile> apkList) {



        Observable.fromIterable(apkList)
                .filter(fileInfo -> {

                    if (FilePathManager.getLocalAppThumbCacheDir().canWrite() && !isContain(FilePathManager.getLocalAppThumbCacheDir(), fileInfo)) {
                        Log.i("wk", "缓存文件中不包含");
                        return true;
                    }
                    return false;
                }).flatMap(new Function<ApkFile, ObservableSource<ApkFile>>() {
            @Override
            public Observable<ApkFile> apply(ApkFile apkFile) throws Exception {
                //Drawable apkDrawable = apkFile.getApkDrawable();
                //if(apkDrawable == null){
                //    Log.i("WW2k", apkFile.getName()+"drawable 为null");
                //}
                return Observable.just(apkFile);
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe(apkFile -> {
                    Bitmap bitmap = FileUtils.drawableToBitmap(apkFile.getApkDrawable());
                    BufferedOutputStream bos = null;
                    try {
                        File appThumb = FilePathManager.getLocalAppThumbCacheFile(apkFile.getName());
                        bos = new BufferedOutputStream(new FileOutputStream(appThumb));
                        if (bitmap == null) {
                            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_app);
                        }
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        CloseUtils.close(bos);
                    }
                });
    }

    /**
     * 判断缓存中是否已经存在
     *
     * @param parent
     * @param apkFile
     * @return
     */
    private static synchronized boolean isContain(File parent, ApkFile apkFile) {
        String[] icos = parent.list();
        for (int i = 0; i < icos.length; i++) {
            if (Md5Utils.getMd5(apkFile.getName()).equals(icos[i])) {
                return true;
            }
        }
        return false;
    }

    public static Observable<List<ApkFile>> asyncLoadApp(Activity activity) {
        return Observable.create((ObservableOnSubscribe<List<ApkFile>>) e -> {
            List<ApkFile> appList = ApkUtils.getApp(activity, Objects.requireNonNull(activity).getPackageManager());
            e.onNext(appList);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());


    }


}
