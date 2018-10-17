package com.merpyzf.xmshare.ui.fragment.transfer;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.interfaces.TransferObserver;
import com.merpyzf.transfermanager.receive.ReceiverManager;
import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.ui.adapter.FileTransferAdapter;
import com.merpyzf.xmshare.util.ApkUtils;
import com.merpyzf.xmshare.util.AppUtils;
import com.merpyzf.xmshare.util.MusicUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class TransferReceiveFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_state)
    TextView mTvState;
    @BindView(R.id.tv_speed)
    TextView mTvSpeed;
    @BindView(R.id.tv_save)
    TextView mTvSave;
    // 显示传输时详细信息的布局
    @BindView(R.id.rl_info)
    RelativeLayout mRlInfo;


    private Unbinder mUnbinder;
    private Context mContext;
    private ReceiverManager mReceiver;
    private String mNickName;
    private List<FileInfo> mTransferFileList;
    private FileTransferAdapter mFileTransferAdapter;
    private static final String TAG = TransferReceiveFragment.class.getSimpleName();
    private long mTotalSize = 0;
    private long mLastFileSize = 0;
    private String mLastFileName;


    @SuppressLint("ValidFragment")
    public TransferReceiveFragment(List<FileInfo> transferFileList) {
        this.mTransferFileList = transferFileList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_transfer_receive, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);
        mContext = getActivity();

        Log.i(TAG, "待传输的文件长度-> " + mTransferFileList.size());
        init();
        initUI();
        initEvent();

        return rootView;
    }

    private void initEvent() {


        /**
         * 监听文件的传输的进度，计算当前传输数据的大小
         */
        ReceiverManager.getInstance(mContext).register(new TransferObserver() {
            @Override
            public void onTransferProgress(FileInfo fileInfo) {

                String[] arrayStr = FileUtils.getFileSizeArrayStr((long) (mTotalSize + fileInfo.getLength() * fileInfo.getProgress()));
                String[] transferSpeed = fileInfo.getTransferSpeed();
                if (null != arrayStr) {
                    mTvSave.setText(arrayStr[0] + arrayStr[1]);
                }

                if (null != transferSpeed) {
                    mTvSpeed.setText(transferSpeed[0] + transferSpeed[1] + "/s");
                }

            }

            @Override
            public void onTransferStatus(FileInfo fileInfo) {

                if (fileInfo instanceof ApkFile) {
                    ArrayList<ApkFile> apkFiles = new ArrayList<>();
                    apkFiles.add((ApkFile) fileInfo);
                    ApkUtils.asyncCacheApkIco(getContext(), apkFiles);
                } else if (fileInfo instanceof MusicFile) {
                    MusicUtils.writeAlbumImg2local(getContext(), fileInfo);
                }

                FileUtils.addFileToMediaStore(getActivity(), new File(fileInfo.getPath()));

                mLastFileSize = fileInfo.getLength();
                mTotalSize += mLastFileSize;

                if (fileInfo.getIsLast() == Const.IS_LAST) {
                    String[] arrayStr = FileUtils.getFileSizeArrayStr((long) (mTotalSize + fileInfo.getLength() * fileInfo.getProgress()));

                    // TODO: 2018/4/18 下面的两个View会偶尔出现空指针问题，原因未查明
                    // 传输完毕的事件
                    if (mRlInfo != null) {
                        mRlInfo.setVisibility(View.GONE);
                    }

                    if (mTvState != null) {
                        mTvState.setText("传输完成, 本次为您节省 " + arrayStr[0] + arrayStr[1] + " 流量 ");
                    }
                }


            }

            @Override
            public void onTransferError(String error) {

            }
        });


        mFileTransferAdapter.setOnItemClickListener((adapter, view, position) -> {


            FileInfo fileInfo = (FileInfo) adapter.getItem(position);

            Log.i("w2k", "待安装文件路径-》" + fileInfo.getPath());

            // -> 调用系统的组件播放

            switch (fileInfo.getType()) {

                case FileInfo.FILE_TYPE_APP:

                    if (fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {

                        AppUtils.installApk(mContext, new File(Environment.getExternalStorageDirectory() + fileInfo.getPath()));

                    } else {
                        Toast.makeText(mContext, "请等待文件传输完毕后再点击安装", Toast.LENGTH_SHORT).show();
                    }

                    break;

                // 点击查看图片
                case FileInfo.FILE_TYPE_IMAGE:

                    break;


                // 点击播放音乐
                case FileInfo.FILE_TYPE_MUSIC:

                    break;

                // 点击播放视频
                case FileInfo.FILE_TYPE_VIDEO:

                    break;
                default:
                    break;


            }

        });


    }


    /**
     * 初始化
     */
    private void init() {

        if (mTransferFileList == null) {
            mTransferFileList = new ArrayList<>();
        }
    }

    /**
     * 初始化UI
     */
    private void initUI() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mFileTransferAdapter = new FileTransferAdapter<>(R.layout.item_rv_transfer, FileTransferAdapter.TYPE_RECEIVE, mTransferFileList);
        mRecyclerView.setAdapter(mFileTransferAdapter);


    }


    @Override
    public void onDestroy() {
        App.getSendFileList().clear();
        // 关闭热点
        App.closeHotspotOnAndroidO();
        super.onDestroy();
    }


}
