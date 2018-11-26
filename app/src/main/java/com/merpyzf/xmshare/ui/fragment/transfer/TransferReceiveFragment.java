package com.merpyzf.xmshare.ui.fragment.transfer;


import android.annotation.SuppressLint;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.observer.TransferObserver;
import com.merpyzf.transfermanager.receive.ReceiverManager;
import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.ui.adapter.FileTransferAdapter;
import com.merpyzf.xmshare.util.AppUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * @author wangke
 */
@SuppressLint("ValidFragment")
public class TransferReceiveFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_state)
    TextView mTvState;
    @BindView(R.id.tv_speed)
    TextView mTvSpeed;
    @BindView(R.id.tv_save)
    TextView mTvSave;
    @BindView(R.id.rl_info)
    RelativeLayout mRlInfo;
    private List<BaseFileInfo> mTransferFileList;
    private FileTransferAdapter mFileTransferAdapter;
    private long mTotalSize = 0;
    private long mLastFileSize = 0;
    private static final String TAG = TransferReceiveFragment.class.getSimpleName();

    @SuppressLint("ValidFragment")
    public TransferReceiveFragment(List<BaseFileInfo> transferFileList) {
        this.mTransferFileList = transferFileList;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_transfer_receive;
    }

    @Override
    protected void initWidget(View rootView) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        mFileTransferAdapter = new FileTransferAdapter(R.layout.item_rv_transfer, FileTransferAdapter.TYPE_RECEIVE, mTransferFileList);
        mRecyclerView.setAdapter(mFileTransferAdapter);
    }

    @Override
    protected void initEvent() {
        ReceiverManager.getInstance(mContext).register(new TransferObserver() {
            @Override
            public void onTransferProgress(BaseFileInfo fileInfo) {
                notifyItemChanged(fileInfo);
                updateTransferSpeed(fileInfo);
            }

            @Override
            public void onTransferStatus(BaseFileInfo fileInfo) {
                notifyItemChanged(fileInfo);
                FileUtils.addFileToMediaStore(getActivity(), new File(fileInfo.getPath()));
                mLastFileSize = fileInfo.getLength();
                mTotalSize += mLastFileSize;
                if (fileInfo.getIsLast() == Const.IS_LAST) {
                    showTransferDataSize();
                }
            }

            @Override
            public void onTransferError(String error) {

            }
        });
        mFileTransferAdapter.setOnItemClickListener((adapter, view, position) -> {
            BaseFileInfo fileInfo = (BaseFileInfo) adapter.getItem(position);
            com.merpyzf.xmshare.util.FileUtils.openFile(mContext, new File(fileInfo.getPath()));
        });
    }

    @Override
    protected void initData() {
        if (mTransferFileList == null) {
            mTransferFileList = new ArrayList<>();
        }
    }

    private void showTransferDataSize() {
        String[] arrayStr = FileUtils.getFileSizeArrayStr((mTotalSize));
        if (mRlInfo != null) {
            // 传输完毕的事件
            mRlInfo.setVisibility(View.GONE);
        }
        if (mTvState != null) {
            if (arrayStr != null) {
                mTvState.setText("传输完成, 本次为您节省 " + arrayStr[0] + arrayStr[1] + " 流量 ");
            }
        }
    }

    private void updateTransferSpeed(BaseFileInfo fileInfo) {
        String[] arrayStr = FileUtils.getFileSizeArrayStr((long) (mTotalSize + fileInfo.getLength() * fileInfo.getProgress()));
        String[] transferSpeed = fileInfo.getTransferSpeed();
        if (null != arrayStr) {
            if (mTvSave == null) {
                mTvSave = mRootView.findViewById(R.id.tv_save);
            }
            mTvSave.setText(arrayStr[0] + arrayStr[1] + "");
        }
        if (null != transferSpeed) {
            if (mTvSpeed == null) {
                mTvSpeed = mRootView.findViewById(R.id.tv_speed);
            }
            mTvSpeed.setText(transferSpeed[0] + transferSpeed[1] + "/s");
        }
    }

    private void notifyItemChanged(BaseFileInfo fileInfo) {
        int position = mFileTransferAdapter.getData().indexOf(fileInfo);
        mFileTransferAdapter.notifyItemChanged(position);
    }

    @Override
    public void onDestroy() {
        App.getTransferFileList().clear();
        App.closeHotspotOnAndroidO();
        super.onDestroy();
    }


}
