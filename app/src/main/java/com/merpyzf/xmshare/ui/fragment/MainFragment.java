package com.merpyzf.xmshare.ui.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.CompactFile;
import com.merpyzf.transfermanager.entity.DocFile;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.FileInfoFactory;
import com.merpyzf.xmshare.bean.model.LitepalFileInfo;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.ui.activity.ReceivedFileActivity;
import com.merpyzf.xmshare.util.ApkUtils;
import com.merpyzf.xmshare.util.FileUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.tv_document_num)
    TextView mTvDocNum;
    @BindView(R.id.tv_apk_num)
    TextView mTvApkNum;
    @BindView(R.id.tv_zip_num)
    TextView mTvcompactNum;
    @BindView(R.id.ll_receive_files)
    LinearLayout mLlReceiveFiles;


    private List<DocFile> mDocFileList = new ArrayList<>();
    private List<ApkFile> mApkFileList = new ArrayList<>();
    private List<CompactFile> mCompactFileList = new ArrayList<>();

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initEvent() {

        int childCount = mLlReceiveFiles.getChildCount();

        for (int i = 0; i < childCount; i++) {

            View view = mLlReceiveFiles.getChildAt(i);

            if (view instanceof LinearLayout) {
                view.setOnClickListener(this);
            }

        }


    }

    /**
     * 加载数据
     */
    @SuppressLint("CheckResult")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initData() {
        super.initData();

        Log.i("fm", "----> loadData方法执行了");

        String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
        Log.i("wk", "内置SD卡路径 -->" + externalStoragePath);
        String rootPath = Environment.getDataDirectory().getPath();
        Log.i("wk", "手机内存根路径 -->" + rootPath);
        String storagePath = getStoragePath(getContext());
        Log.i("wk", "手机扩展内存卡路径 -->" + storagePath);


        // 两者都进行更新
        int count = LitepalFileInfo.count(LitepalFileInfo.class);

        if (count == 0) {

            mTvApkNum.setText("扫描中...");
            mTvDocNum.setText("扫描中...");
            mTvcompactNum.setText("扫描中...");

        } else {

            updateUIFromCache();

        }


        Observable.create((ObservableOnSubscribe<String>) e -> {

            List<FileInfo> scanResults = FileUtils.traverseFolder(externalStoragePath);
            // 进行数据库缓存
            List<LitepalFileInfo> litepalFileInfos = LitepalFileInfo.findAll(LitepalFileInfo.class);

            Log.i("wk", "scanResult size-->" + scanResults.size());
            for (int i = 0; i < scanResults.size(); i++) {

                // 不包含的时候才将其作为缓存写入到数据库中
                if (!isContainInCache(scanResults.get(i), litepalFileInfos)) {

                    LitepalFileInfo litepalFileInfo = FileInfoFactory.toLitepalFileInfoType(scanResults.get(i));
                    litepalFileInfo.save();
                    Log.i("wk", "将文件写入到数据库了" + litepalFileInfo.getPath());


                }

            }

            mApkFileList.clear();
            mDocFileList.clear();
            mCompactFileList.clear();

            // 直接将原来的列表清空然后用新获取的值替换
            for (int i = 0; i < scanResults.size(); i++) {
                FileInfo fileInfo = scanResults.get(i);
                switch (fileInfo.getType()) {
                    case FileInfo.FILE_TYPE_APP:
                        mApkFileList.add((ApkFile) fileInfo);
                        break;
                    case FileInfo.FILE_TYPE_DOCUMENT:
                        mDocFileList.add((DocFile) fileInfo);
                        break;
                    case FileInfo.FILE_TYPE_COMPACT:
                        mCompactFileList.add((CompactFile) fileInfo);
                        break;
                    default:
                        break;
                }
            }
            e.onNext("ok");
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (null != mTvApkNum) {
                        mTvApkNum.setText(String.valueOf(mApkFileList.size()));
                    }
                    if (null != mTvDocNum) {
                        mTvDocNum.setText(String.valueOf(mDocFileList.size()));
                    }
                    if (null != mTvcompactNum) {
                        mTvcompactNum.setText(String.valueOf(mCompactFileList.size()));
                    }
                    // 异步缓存ico的图标
                    ApkUtils.asyncCacheApkIco(getContext(), mApkFileList);
                });

    }

    /**
     * 判断扫描的文件是否已经在数据库中
     *
     * @param fileInfo         源文件
     * @param litepalFileInfos 从数据库中读取到的集合
     */
    private boolean isContainInCache(FileInfo fileInfo, List<LitepalFileInfo> litepalFileInfos) {

        boolean isContain = false;

        for (LitepalFileInfo litepalFileInfo : litepalFileInfos) {

            if (litepalFileInfo.getPath().equals(fileInfo.getPath())) {
                isContain = true;
            }
        }
        return isContain;
    }

    /**
     * 读取文件数据库缓存并更新UI
     */
    @SuppressLint("CheckResult")
    private void updateUIFromCache() {

        mCompactFileList.clear();
        mApkFileList.clear();
        mDocFileList.clear();

        List<LitepalFileInfo> litepalFileInfos = LitepalFileInfo.findAll(LitepalFileInfo.class);

        Observable.fromIterable(litepalFileInfos)
                .filter(litepalFileInfo -> {
                    if (litepalFileInfo.getType() == FileInfo.FILE_TYPE_APP) {
                        if (!new File(litepalFileInfo.getPath()).exists()) {
                            deleteCache(litepalFileInfo);
                            Log.i("w2k", "文件不存在删除-->" + litepalFileInfo.getPath());
                            return false;
                        }
                        return true;
                    }
                    return false;
                }).subscribeOn(Schedulers.io())
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(litepalFileInfo -> {
                    mApkFileList.add(FileInfoFactory.toApkFileType(litepalFileInfo));
                    mTvApkNum.setText(String.valueOf(mApkFileList.size()));
                });


        Observable.fromIterable(litepalFileInfos)
                .filter(litepalFileInfo -> {
                    if (litepalFileInfo.getType() == FileInfo.FILE_TYPE_DOCUMENT) {
                        if (!new File(litepalFileInfo.getPath()).exists()) {
                            deleteCache(litepalFileInfo);
                            Log.i("w2k", "文件不存在删除-->" + litepalFileInfo.getPath());
                            return false;
                        }
                        return true;
                    }
                    return false;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(litepalFileInfo -> {

                    mDocFileList.add(FileInfoFactory.toDocFileType(litepalFileInfo));
                    mTvDocNum.setText(String.valueOf(mDocFileList.size()));
                });

        Observable.fromIterable(litepalFileInfos)
                .filter(litepalFileInfo -> {
                    if (litepalFileInfo.getType() == FileInfo.FILE_TYPE_COMPACT) {

                        if (!new File(litepalFileInfo.getPath()).exists()) {
                            deleteCache(litepalFileInfo);
                            Log.i("w2k", "文件不存在删除-->" + litepalFileInfo.getPath());
                            return false;
                        }
                        return true;
                    }
                    return false;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(litepalFileInfo -> {

                    mCompactFileList.add(FileInfoFactory.toCompactFileType(litepalFileInfo));
                    mTvcompactNum.setText(String.valueOf(mCompactFileList.size()));

                });


    }

    /**
     * 从数据库中删除缓存
     *
     * @param litepalFileInfo
     */
    private void deleteCache(LitepalFileInfo litepalFileInfo) {
        Cursor cursor = LitepalFileInfo.findBySQL("select *from litepalfileinfo where path = ?", litepalFileInfo.getPath());
        if (cursor.getCount() == 1) {
            cursor.moveToNext();
            int id = Integer.valueOf(cursor.getString(cursor.getColumnIndex("id")));
            // 若文件不存在则删除其在数据库中的缓存
            LitepalFileInfo.delete(LitepalFileInfo.class, id);
        }


    }


    /**
     * 对View进行相关的初始化工作
     *
     * @param rootView
     */
    @Override
    protected void initWidget(View rootView) {
        Log.i("fm", "----> initView方法执行了");
    }

    /**
     * 获取扩展内存的路径
     *
     * @param mContext
     * @return
     */
    public String getStoragePath(Context mContext) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), ReceivedFileActivity.class);
        switch (v.getId()) {
            case R.id.ll_app:
                intent.putExtra("fileType", FileInfo.FILE_TYPE_APP);
                break;
            case R.id.ll_image:
                intent.putExtra("fileType", FileInfo.FILE_TYPE_IMAGE);
                break;
            case R.id.ll_music:
                intent.putExtra("fileType", FileInfo.FILE_TYPE_MUSIC);
                break;
            case R.id.ll_video:
                intent.putExtra("fileType", FileInfo.FILE_TYPE_VIDEO);
                break;
            case R.id.ll_other:
                intent.putExtra("fileType", FileInfo.FILE_TYPE_OTHER);
                break;
            default:
                break;
        }
        startActivity(intent);
    }
}
