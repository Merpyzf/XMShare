package com.merpyzf.xmshare.ui.fragment.transfer;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.Peer;
import com.merpyzf.transfermanager.interfaces.TransferObserver;
import com.merpyzf.transfermanager.send.SenderManager;
import com.merpyzf.transfermanager.util.FileUtils;
import com.merpyzf.transfermanager.util.ToastUtils;
import com.merpyzf.transfermanager.util.WifiMgr;
import com.merpyzf.xmshare.App;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.ui.adapter.FileTransferAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 文件发送列表进度及状态展示界面
 *
 * @author wangke
 */
@SuppressLint("ValidFragment")
public class TransferSendFragment extends Fragment {

    @BindView(R.id.rv_send_list)
    RecyclerView mRvSendList;
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
    private FileTransferAdapter<FileInfo> mFileTransferAdapter;
    private String mNickName;
    private WifiMgr mWifiMgr;
    private Peer mPeer;
    private long mTotalSize = 0;
    private long mLastFileSize = 0;
    private static final String TAG = TransferSendFragment.class.getSimpleName();

    @SuppressLint("ValidFragment")
    public TransferSendFragment(Peer peer) {
        this.mPeer = peer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_transfer_send, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        mContext = getActivity();
        initUI();
        initEvent(rootView);

        mFileTransferAdapter = new FileTransferAdapter<>(R.layout.item_rv_transfer,
                FileTransferAdapter.TYPE_SEND, App.getSendFileList());
        mRvSendList.setAdapter(mFileTransferAdapter);

        return rootView;
    }

    private void initEvent(View rootView) {

        /**
         * 监听文件的传输的进度，计算当前传输数据的大小
         */
        SenderManager.getInstance(getContext()).register(new TransferObserver() {
            @Override
            public void onTransferProgress(FileInfo fileInfo) {
                String[] arrayStr = FileUtils.getFileSizeArrayStr((long) (mTotalSize + fileInfo.getLength() * fileInfo.getProgress()));
                String[] transferSpeed = fileInfo.getTransferSpeed();
                // TODO: 2018/4/18 不明白此处为什么会出现 nullpointer expection,当传输的文件比较多的时候会偶尔触发，暂时通过下面的方法解决这个问题
                if (null != arrayStr) {
                    if (mTvSave == null) {
                        mTvSave = rootView.findViewById(R.id.tv_save);
                    }
                    mTvSave.setText(arrayStr[0] + arrayStr[1] + "");
                }

                if (null != transferSpeed) {
                    if (mTvSpeed == null) {
                        mTvSpeed = rootView.findViewById(R.id.tv_speed);
                    }
                    mTvSpeed.setText(transferSpeed[0] + transferSpeed[1] + "/s");
                }


            }

            @Override
            public void onTransferStatus(FileInfo fileInfo) {

                mLastFileSize = fileInfo.getLength();
                mTotalSize += mLastFileSize;

                if (fileInfo.getIsLast() == Const.IS_LAST) {
                    String[] arrayStr = FileUtils.getFileSizeArrayStr((long) (mTotalSize + fileInfo.getLength() * fileInfo.getProgress()));

                    // TODO: 2018/4/25   下面的两个View会偶尔出现空指针异常，当Fragment销毁的时候应该要及时地移除监听
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


            }
            @Override
            public void onTransferError(String error) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 当View创建完毕后进行文件的发送
        SenderManager.getInstance(mContext).send(mPeer.getHostAddress(), App.getSendFileList());
        super.onViewCreated(view, savedInstanceState);


    }

    private void initUI() {

        mRvSendList.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void onDestroy() {
        mUnbinder.unbind();
        SenderManager.getInstance(mContext).release();
        App.resetSelectedFilesStatus();
        super.onDestroy();
    }


}
