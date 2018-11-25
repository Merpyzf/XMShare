package com.merpyzf.xmshare.ui.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.CompactFile;
import com.merpyzf.transfermanager.entity.DocFile;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.factory.FileInfoFactory;
import com.merpyzf.xmshare.bean.Volume;
import com.merpyzf.xmshare.bean.model.LitepalFileInfo;
import com.merpyzf.xmshare.common.Const;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.ui.activity.ReceivedFileActivity;
import com.merpyzf.xmshare.ui.adapter.VolumeAdapter;
import com.merpyzf.xmshare.ui.fragment.filemanager.FileManagerFragment;
import com.merpyzf.xmshare.util.StorageUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FunctionListFragment extends BaseFragment implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.tv_document_num)
    TextView mTvDocNum;
    @BindView(R.id.tv_apk_num)
    TextView mTvApkNum;
    @BindView(R.id.tv_zip_num)
    TextView mTvcompactNum;
    @BindView(R.id.ll_receive_files)
    LinearLayout mLlReceiveFiles;
    @BindView(R.id.rl_volume)
    RecyclerView mRlVolume;

    private List<DocFile> mDocFileList = new ArrayList<>();
    private List<ApkFile> mApkFileList = new ArrayList<>();
    private List<CompactFile> mCompactFileList = new ArrayList<>();
    private ArrayList<Volume> mVolumes = new ArrayList<>();
    private VolumeAdapter mVolumeAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_function_list;
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
        mVolumeAdapter.setOnItemClickListener(this);
    }

    /**
     * 加载数据
     */
    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        super.initData();
        mVolumes.addAll(StorageUtils.getVolumes(getContext()));
        mVolumeAdapter.notifyDataSetChanged();
        //// 两者都进行更新
        //int count = LitepalFileInfo.count(LitepalFileInfo.class);
        //if (count == 0) {
        //    mTvApkNum.setText("扫描中...");
        //    mTvDocNum.setText("扫描中...");
        //    mTvcompactNum.setText("扫描中...");
        //} else {
        //    updateUIFromCache();
        //}
        //Observable.create((ObservableOnSubscribe<String>) e -> {
        //    List<BaseFileInfo> scanResults = FileUtils.traverseFolder(mInnerStoragePath);
        //    // 进行数据库缓存
        //    List<LitepalFileInfo> litepalFileInfos = LitepalFileInfo.findAll(LitepalFileInfo.class);
        //    for (int i = 0; i < scanResults.size(); i++) {
        //        // 不包含的时候才将其作为缓存写入到数据库中
        //        if (!isContainInCache(scanResults.get(i), litepalFileInfos)) {
        //            LitepalFileInfo litepalFileInfo = FileInfoFactory.toLitepalFileInfoType(scanResults.get(i));
        //            litepalFileInfo.save();
        //        }
        //    }
        //    mApkFileList.clear();
        //    mDocFileList.clear();
        //    mCompactFileList.clear();
        //
        //    // 直接将原来的列表清空然后用新获取的值替换
        //    for (int i = 0; i < scanResults.size(); i++) {
        //        BaseFileInfo fileInfo = scanResults.get(i);
        //        switch (fileInfo.getType()) {
        //            case BaseFileInfo.FILE_TYPE_APP:
        //                mApkFileList.add((ApkFile) fileInfo);
        //                break;
        //            case BaseFileInfo.FILE_TYPE_DOCUMENT:
        //                mDocFileList.add((DocFile) fileInfo);
        //                break;
        //            case BaseFileInfo.FILE_TYPE_COMPACT:
        //                mCompactFileList.add((CompactFile) fileInfo);
        //                break;
        //            default:
        //                break;
        //        }
        //    }
        //    e.onNext("ok");
        //})
        //        .subscribeOn(Schedulers.io())
        //        .observeOn(AndroidSchedulers.mainThread())
        //        .subscribe(s -> {
        //            if (null != mTvApkNum) {
        //                mTvApkNum.setText(String.valueOf(mApkFileList.size()));
        //            }
        //            if (null != mTvDocNum) {
        //                mTvDocNum.setText(String.valueOf(mDocFileList.size()));
        //            }
        //            if (null != mTvcompactNum) {
        //                mTvcompactNum.setText(String.valueOf(mCompactFileList.size()));
        //            }
        //            // 异步缓存ico的图标
        //            ApkUtils.asyncCacheApkIco(getContext(), mApkFileList);
        //        });

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
                    if (litepalFileInfo.getType() == BaseFileInfo.FILE_TYPE_APP) {
                        if (!new File(litepalFileInfo.getPath()).exists()) {
                            deleteCache(litepalFileInfo);
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
                    if (litepalFileInfo.getType() == BaseFileInfo.FILE_TYPE_DOCUMENT) {
                        if (!new File(litepalFileInfo.getPath()).exists()) {
                            deleteCache(litepalFileInfo);
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
                    if (litepalFileInfo.getType() == BaseFileInfo.FILE_TYPE_COMPACT) {
                        if (!new File(litepalFileInfo.getPath()).exists()) {
                            deleteCache(litepalFileInfo);
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
        mRlVolume.setLayoutManager(new LinearLayoutManager(mContext));
        mVolumeAdapter = new VolumeAdapter(mContext, R.layout.item_rv_volume, mVolumes);
        mRlVolume.setAdapter(mVolumeAdapter);

    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), ReceivedFileActivity.class);
        switch (v.getId()) {
            case R.id.ll_app:
                intent.putExtra("fileType", BaseFileInfo.FILE_TYPE_APP);
                startActivity(intent);
                break;
            case R.id.ll_image:
                intent.putExtra("fileType", BaseFileInfo.FILE_TYPE_IMAGE);
                startActivity(intent);
                break;
            case R.id.ll_music:
                intent.putExtra("fileType", BaseFileInfo.FILE_TYPE_MUSIC);
                startActivity(intent);
                break;
            case R.id.ll_video:
                intent.putExtra("fileType", BaseFileInfo.FILE_TYPE_VIDEO);
                startActivity(intent);
                break;
            case R.id.ll_other:
                intent.putExtra("fileType", BaseFileInfo.FILE_TYPE_STORAGE);
                startActivity(intent);
                break;

            default:
                break;
        }

    }

    /**
     * 判断扫描的文件是否已经在数据库中
     *
     * @param fileInfo         源文件
     * @param litepalFileInfos 从数据库中读取到的集合
     */
    private boolean isContainInCache(BaseFileInfo fileInfo, List<LitepalFileInfo> litepalFileInfos) {

        boolean isContain = false;

        for (LitepalFileInfo litepalFileInfo : litepalFileInfos) {
            if (litepalFileInfo.getPath().equals(fileInfo.getPath())) {
                isContain = true;
            }
        }
        return isContain;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Volume volume = (Volume) adapter.getItem(position);
        if (volume.isRemovable()) {
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager().beginTransaction();
            FileManagerFragment fileManagerFragment = new FileManagerFragment();
            Bundle bundle = new Bundle();
            bundle.putCharSequence("rootPath", volume.getPath());
            bundle.putCharSequence("volumeName", "SD卡");
            fileManagerFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fl_main_container, fileManagerFragment, Const.TAG_FILE_MANAGER);
            fragmentTransaction.commit();
        } else {
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager().beginTransaction();
            FileManagerFragment fileManagerFragment = new FileManagerFragment();
            Bundle bundle = new Bundle();
            bundle.putCharSequence("rootPath", volume.getPath());
            bundle.putCharSequence("volumeName", "手机存储");
            fileManagerFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fl_main_container, fileManagerFragment, Const.TAG_FILE_MANAGER);
            fragmentTransaction.commit();
        }
    }
}
