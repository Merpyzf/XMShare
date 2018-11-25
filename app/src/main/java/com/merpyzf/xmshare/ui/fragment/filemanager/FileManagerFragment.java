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
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.bean.FileInfo;
import com.merpyzf.xmshare.bean.factory.FileInfoFactory;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.observer.FilesStatusObservable;
import com.merpyzf.xmshare.observer.FilesStatusObserver;
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
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.merpyzf.xmshare.util.UiUtils.getRecyclerViewLastPosition;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author wangke
 */
public class FileManagerFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, IndicatorChangedCallback, BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R.id.rv_file_list)
    RecyclerView mRvFileList;
    @BindView(R.id.select_indicator)
    SelectIndicatorView mSelectIndicator;
    @BindView(R.id.view_underline)
    View mViewUnderLine;
    private String mRootPath;
    private List<FileInfo> mFileList;
    private FileManagerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Indicator mCurrIndicator;
    private static String TAG = FileManagerFragment.class.getSimpleName();

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
        mLayoutManager = new LinearLayoutManager(mContext);
        mRvFileList.setLayoutManager(mLayoutManager);
        mRvFileList.addItemDecoration(new DirItemDecotation());
        mFileList = new ArrayList<>();
        mAdapter = new FileManagerAdapter(R.layout.item_fileinfo, mFileList);
        View emptyView = View.inflate(mContext, R.layout.view_rv_file_empty, null);
        mAdapter.setEmptyView(emptyView);
        mRvFileList.setAdapter(mAdapter);

    }

    @Override
    protected void initData() {
        super.initData();
        loadingDirectory(mRootPath);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
        mSelectIndicator.setIndicatorClickCallBack(this);
        mRvFileList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mRvFileList.canScrollVertically(-1)) {
                    if (mViewUnderLine.getVisibility() == View.INVISIBLE) {
                        mViewUnderLine.setVisibility(View.VISIBLE);
                    }
                } else {
                    mViewUnderLine.setVisibility(View.INVISIBLE);
                }

            }
        });
        FilesStatusObservable.getInstance().register(TAG, new FilesStatusObserver() {
            @Override
            public void onSelected(com.merpyzf.transfermanager.entity.FileInfo fileInfo) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelSelected(com.merpyzf.transfermanager.entity.FileInfo fileInfo) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSelectedAll(List<com.merpyzf.transfermanager.entity.FileInfo> fileInfoList) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelSelectedAll(List<com.merpyzf.transfermanager.entity.FileInfo> fileInfoList) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * 加载目录中的文件
     *
     * @param dir
     */
    public void loadingDirectory(String dir) {
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
                    String suffix = FileUtils.getFileSuffix(fileInfo.getPath()).toLowerCase();
                    fileInfo.setSuffix(suffix);
                    fileInfo.setPhoto(FileTypeHelper.isPhotoType(suffix));
                    Log.i("WW2k", "name-> " + fileInfo.getName());
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
                            } else if (o1.getFirstLetter() == o2.getFirstLetter()) {
                                return 0;
                            } else {
                                return -1;
                            }
                        });
                        // 按照文件夹在前文件在后的顺序添加到集合中去
                        mFileList.addAll(tempDirs);
                        mFileList.addAll(tempFiles);
                        mAdapter.notifyDataSetChanged();
                        if (mCurrIndicator != null) {
                            int[] lastPos = (int[]) mCurrIndicator.getTag();
                            mLayoutManager.scrollToPositionWithOffset(lastPos[0], lastPos[1]);
                        }
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
    public void onIndicatorChanged(Indicator indicator) {
        loadingDirectory(indicator.getValue());
        mCurrIndicator = indicator;

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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        FileInfo fileInfo = (FileInfo) adapter.getItem(position);
        if (fileInfo.isDirectory()) {
            if (App.isContain(fileInfo)) {
                App.removeTransferFileByPath(fileInfo.getPath());
                mAdapter.notifyItemChanged(position);
            } else {
                loadingDirectory(fileInfo.getPath());
                Indicator indicator = new Indicator(fileInfo.getName(), fileInfo.getPath());
                int[] lastPosition = getRecyclerViewLastPosition(mLayoutManager);
                mSelectIndicator.setScrollPosTag(lastPosition);
                mSelectIndicator.addIndicator(indicator);
            }
        }

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        FileInfo fileInfo = (FileInfo) adapter.getItem(position);
        if (App.isContain(fileInfo)) {
            App.removeTransferFileByPath(fileInfo.getPath());
            FilesStatusObservable.getInstance().notifyObservers((com.merpyzf.transfermanager.entity.FileInfo) null, TAG, FilesStatusObservable.FILE_CANCEL_SELECTED);
        } else {
            com.merpyzf.transfermanager.entity.FileInfo transferFileInfo = FileInfoFactory.toTransferFileInfo(fileInfo);
            App.addTransferFile(transferFileInfo);
            // 将文件选择的事件回调给外部
            FilesStatusObservable.getInstance().notifyObservers(transferFileInfo, TAG, FilesStatusObservable.FILE_SELECTED);
        }
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onDestroy() {
        FilesStatusObservable.getInstance().remove(TAG);
        super.onDestroy();
    }
}
