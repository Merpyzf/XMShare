package com.merpyzf.xmshare.ui.fragment.transfer;


import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merpyzf.common.utils.PersonalSettingUtils;
import com.merpyzf.common.utils.SharedPreUtils;
import com.merpyzf.common.utils.ToastUtils;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.Peer;
import com.merpyzf.transfermanager.observer.AbsTransferObserver;
import com.merpyzf.transfermanager.send.SenderManager;
import com.merpyzf.transfermanager.utils.FileUtils;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseFragment;
import com.merpyzf.xmshare.observer.PersonalObservable;
import com.merpyzf.xmshare.ui.adapter.FileTransferAdapter;

import butterknife.BindView;

/**
 * 文件发送列表进度及状态展示界面
 *
 * @author wangke
 */
@SuppressLint("ValidFragment")
public class TransferSendFragment extends BaseFragment {

    @BindView(R.id.rv_send_list)
    RecyclerView mRvSendList;
    @BindView(R.id.tv_state)
    TextView mTvState;
    @BindView(R.id.tv_speed)
    TextView mTvSpeed;
    @BindView(R.id.tv_save)
    TextView mTvSave;
    @BindView(R.id.rl_info)
    RelativeLayout mRlInfo;
    private FileTransferAdapter<BaseFileInfo> mFileTransferAdapter;
    private Peer mPeer;
    private long mTotalSize = 0;
    private long mLastFileSize = 0;
    private static final String TAG = TransferSendFragment.class.getSimpleName();

    @SuppressLint("ValidFragment")
    public TransferSendFragment(Peer peer) {
        this.mPeer = peer;
    }

    @Override
    protected void doCreateEvent() {
        SenderManager.getInstance(getContext()).register(new AbsTransferObserver() {
            @Override
            public void onTransferProgress(BaseFileInfo fileInfo) {
                notifyItemChanged(fileInfo);
                updateTransferSpeed(fileInfo);
            }

            @Override
            public void onTransferStatus(BaseFileInfo fileInfo) {
                notifyItemChanged(fileInfo);
                // 记录本次文件传输的字节数
                mLastFileSize = fileInfo.getLength();
                mTotalSize += mLastFileSize;
                PersonalSettingUtils.updateSavedNetFlow(mContext, mLastFileSize);
                if (fileInfo.getIsLast() == Const.IS_LAST) {
                    showTransferDataSize();
                }
            }

            @Override
            public void onTransferError(String error) {
                if (mTvState != null && mTvSave != null && mTvSpeed != null) {
                    mTvState.setText("sorry! 传输被意外中断...");
                    mTvSave.setVisibility(View.GONE);
                    mTvSpeed.setVisibility(View.GONE);
                }
            }
        });
        // 传输前先重置文件的状态
        App.resetSelectedFilesStatus();
        SenderManager.getInstance(mContext).send(mPeer.getHostAddress(), App.getTransferFileList());
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
        // TODO: 2018/4/18 不明白此处为什么会出现 nullpointer expection,当传输的文件比较多的时候会偶尔触发，暂时通过下面的方法解决这个问题
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
    protected int getContentLayoutId() {
        return R.layout.fragment_transfer_send;
    }

    @Override
    protected void doCreateView(View rootView) {
        super.doCreateView(rootView);
        mRvSendList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvSendList.getItemAnimator().setChangeDuration(0);
        mFileTransferAdapter = new FileTransferAdapter(R.layout.item_rv_transfer,
                FileTransferAdapter.TYPE_SEND, App.getTransferFileList());
        mRvSendList.setAdapter(mFileTransferAdapter);
    }

    @Override
    public void onDestroy() {
        // 更新用户信息(累计节省的流量)
        PersonalObservable.getInstance().notifyAllObserver();
        SenderManager.getInstance(mContext).release();
        super.onDestroy();
    }


}
