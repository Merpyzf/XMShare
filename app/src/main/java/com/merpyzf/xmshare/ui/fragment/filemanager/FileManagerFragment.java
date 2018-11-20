package com.merpyzf.xmshare.ui.fragment.filemanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.promeg.pinyinhelper.Pinyin;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.FileInfo;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.ui.adapter.FileManagerAdapter;
import com.merpyzf.xmshare.ui.fragment.FunctionListFragment;
import com.merpyzf.xmshare.ui.widget.DirItemDecotation;
import com.merpyzf.xmshare.ui.widget.IndicatorChangedCallback;
import com.merpyzf.xmshare.ui.widget.SelectIndicatorView;
import com.merpyzf.xmshare.ui.widget.bean.Indicator;
import com.merpyzf.xmshare.util.FileTypeHelper;
import com.merpyzf.xmshare.util.FileUtils;
import com.merpyzf.xmshare.util.SettingHelper;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class FileManagerFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, IndicatorChangedCallback {

    @BindView(R.id.rv_file_list)
    RecyclerView mRvFileList;
    @BindView(R.id.select_indicator)
    SelectIndicatorView mSelectIndicator;
    private String mRootPath;
    private List<FileInfo> mFileList;
    private FileManagerAdapter mAdapter;

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mRootPath = (String) bundle.getCharSequence("rootPath");
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_file_manager;
    }

    @Override
    protected void initWidget(View rootView) {
        super.initWidget(rootView);
        mSelectIndicator.addIndicator(new Indicator("/", mRootPath));
        mRvFileList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvFileList.addItemDecoration(new DirItemDecotation());
        mFileList = new ArrayList<>();
        mAdapter = new FileManagerAdapter(R.layout.item_fileinfo, mFileList);
        mRvFileList.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        loadDir(mRootPath);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mAdapter.setOnItemClickListener(this);
        mSelectIndicator.setIndicatorClickCallBack(this);
    }


    /**
     * 加载目录中的文件
     *
     * @param dir
     */
    public void loadDir(String dir) {
        File file = new File(dir);
        ArrayList<FileInfo> tempDirs = new ArrayList();
        ArrayList<FileInfo> tempFiles = new ArrayList();
        mFileList.clear();
        Observable.fromArray(file.listFiles())
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .filter(fileInfo -> {
                    boolean isShow = SettingHelper.showHiddenFile(mContext);
                    if (!isShow) {
                        if (fileInfo.getName().startsWith(".")) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(f -> {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setDirectory(f.isDirectory());
                    fileInfo.setName(f.getName());
                    fileInfo.setPath(f.getPath());
                    fileInfo.setPhoto(FileUtils.isPhoto(f));
                    fileInfo.setSize(f.length());
                    fileInfo.setFirstLetter(getFirstLetter(fileInfo.getName()));
                    fileInfo.setPhoto(FileTypeHelper.isPhotoType(FileUtils.getFileSuffix(fileInfo.getPath())));
                    return fileInfo;
                })

                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FileInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FileInfo fileInfo) {
                        if (fileInfo.isDirectory()) {
                            tempDirs.add(fileInfo);
                        } else {
                            tempFiles.add(fileInfo);
                        }
                        Log.i("WW2k", fileInfo.getFirstLetter() + "");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Collections.sort(tempDirs, (o1, o2) -> {
                            if (o1.getFirstLetter() > o2.getFirstLetter()) {
                                return 1;
                            } else if (o1.getFirstLetter() < o2.getFirstLetter()) {
                                return -1;
                            }
                            return 0;
                        });
                        Collections.sort(tempFiles, (o1, o2) -> {
                            if (o1.getFirstLetter() > o2.getFirstLetter()) {
                                return 1;
                            } else {
                                return -1;
                            }
                        });
                        // 按照文件夹在前文件在后的顺序添加到集合中去
                        mFileList.addAll(tempDirs);
                        mFileList.addAll(tempFiles);
                        mAdapter.notifyDataSetChanged();
                    }
                });

    }

    /**
     * 根据文件名获取第一个字符所对应的首字母
     *
     * @param name
     * @return
     */
    private char getFirstLetter(String name) {
        char firstLetter;
        // 判断是否是隐藏文件
        if (name.startsWith(".")) {
            // 如果是则取第二个字符
            firstLetter = Character.toLowerCase(Pinyin.toPinyin(name.charAt(1)).charAt(0));
        } else {
            // 如果不是则直接取第一个字符
            firstLetter = Character.toLowerCase(Pinyin.toPinyin(name.charAt(0)).charAt(0));
        }
        return firstLetter;
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        FileInfo fileInfo = (FileInfo) adapter.getItem(position);
        if (fileInfo.isDirectory()) {
            loadDir(fileInfo.getPath());
            Indicator indicator = new Indicator(fileInfo.getName(), fileInfo.getPath());
            mSelectIndicator.addIndicator(indicator);
        }
    }

    @Override
    public void onIndicatorChanged(Indicator indicator) {
        loadDir(indicator.getValue());
    }

    public void onBackPressed() {
        if (mSelectIndicator.isRoot()) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fl_main_container, new FunctionListFragment());
            transaction.commit();
        } else {
            mSelectIndicator.pop();
        }
    }
}
