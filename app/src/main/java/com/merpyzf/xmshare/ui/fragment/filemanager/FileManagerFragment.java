package com.merpyzf.xmshare.ui.fragment.filemanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.FileInfo;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.ui.adapter.FileManagerAdapter;
import com.merpyzf.xmshare.ui.fragment.FunctionListFragment;
import com.merpyzf.xmshare.ui.widget.DirItemDecotation;
import com.merpyzf.xmshare.ui.widget.IndicatorChangedCallback;
import com.merpyzf.xmshare.ui.widget.SelectIndicatorView;
import com.merpyzf.xmshare.ui.widget.bean.Indicator;
import com.merpyzf.xmshare.util.FileUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
        mFileList.clear();
        Observable.fromArray(file.listFiles())
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .map(file1 -> {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setDirectory(file1.isDirectory());
                    fileInfo.setName(file1.getName());
                    fileInfo.setPath(file1.getPath());
                    fileInfo.setPhoto(FileUtils.isPhoto(file1));
                    fileInfo.setSize(file1.length());
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
                        mFileList.add(fileInfo);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mAdapter.notifyDataSetChanged();
                    }
                });

    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Log.i("WW2k", "被点击了");
        FileInfo fileInfo = (FileInfo) adapter.getItem(position);
        Indicator indicator = new Indicator(fileInfo.getName(), fileInfo.getPath());
        mSelectIndicator.addIndicator(indicator);
        loadDir(fileInfo.getPath());

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

    @Override
    public void onIndicatorChanged(Indicator indicator) {
        loadDir(indicator.getValue());
    }
}
