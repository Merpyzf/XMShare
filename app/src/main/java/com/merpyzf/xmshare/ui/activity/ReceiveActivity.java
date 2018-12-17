package com.merpyzf.xmshare.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.merpyzf.common.manager.ThreadPoolManager;
import com.merpyzf.common.utils.PersonalSettingUtils;
import com.merpyzf.transfermanager.PeerManager;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.Peer;
import com.merpyzf.transfermanager.observer.AbsTransferObserver;
import com.merpyzf.transfermanager.receive.ReceiverManager;
import com.merpyzf.common.utils.ApManager;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.observer.AbsFileStatusObserver;
import com.merpyzf.xmshare.ui.fragment.ReceivePeerFragment;
import com.merpyzf.xmshare.ui.fragment.transfer.TransferReceiveFragment;
import com.merpyzf.common.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 接收端:
 * <p>
 * 1. 定时发送广播信息，用于发送端对接收端设备的发现
 * 2. 需要开启一个UDPServer,用来显示需要进行连接的发送端的设备，点击发送端设备头像以完成连接确认
 * 3. 当接收端退出的时候,需要发送一个离线广播
 *
 * @author wangke
 */
public class ReceiveActivity extends BaseActivity {
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    private ReceivePeerFragment mReceivePeerFragment;
    private TransferReceiveFragment mTransferReceiveFragment;
    /**
     * 标记当前是否正在进行文件传输
     */
    private boolean isTransfering = false;
    private static final String TAG = ReceiveActivity.class.getSimpleName();

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_receive;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mReceivePeerFragment = new ReceivePeerFragment();
        transaction.replace(R.id.frame_content, mReceivePeerFragment);
        transaction.commit();

    }


    @Override
    protected void initToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("我要接收");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void doCreateEvent() {
        if (mReceivePeerFragment != null) {
            mReceivePeerFragment.setOnReceivePairActionListener(new ReceivePeerFragment.OnReceivePairActionListener() {
                @Override
                public void onRequestSendFileAction() {
                    // 开启一个用于文件接收的server等待客户端的连接
                    startFileReceiveServer();
                    ReceiverManager.getInstance(mContext).register(new FileTransferListener());
                    // 当文件列表接收完毕时，开始加载展示文件传输列表的Fragment
                    ReceiverManager.getInstance(mContext).setOnTransferFileListListener(transferFileList -> {
                        // 当开始传输文件的时候才开始监听对端传输过程中的中断动作
                        loadReceiveFrg(transferFileList);
                        isTransfering = true;
                    });
                }
                // 热点下的事件
                @Override
                public void onApEnableAction() {
                    // 开启一个用于文件接收的server
                    startFileReceiveServer();
                    ReceiverManager.getInstance(mContext).register(new FileTransferListener());
                    // 监听待传输的文件列表是否发送成功
                    ReceiverManager.getInstance(mContext).setOnTransferFileListListener(transferFileList -> {
                        loadReceiveFrg(transferFileList);
                        isTransfering = true;
                    });
                }
            });
        }
    }

    private void loadReceiveFrg(List<BaseFileInfo> transferFileList) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mTransferReceiveFragment = new TransferReceiveFragment(transferFileList);
        transaction.replace(R.id.frame_content, mTransferReceiveFragment);
        transaction.commit();
    }


    private void startFileReceiveServer() {
        ReceiverManager receiverManager = ReceiverManager.getInstance(mContext);
        ToastUtils.showShort(mContext, "开启server等待连接");
        ThreadPoolManager.getInstance().execute(receiverManager);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    class FileTransferListener extends AbsTransferObserver {
        @Override
        public void onTransferStatus(BaseFileInfo fileInfo) {
            // 如果当前传输的是最后一个文件，并且传输成功后重置标记
            if (fileInfo.getIsLast() == Const.IS_LAST && fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {
                isTransfering = false;
            }
        }

        @Override
        public void onTransferError(String error) {
            isTransfering = false;
            ToastUtils.showLong(mContext, "ლ(╹◡╹ლ) sorry! 传输被意外终止: \n" + error);
            int closePageMode = PersonalSettingUtils.getIsCloseCurrPageWhenError(mContext);
            if (closePageMode == PersonalSettingUtils.CLOSE_CURRENT_PAGE_WHEN_ERROR) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isTransfering) {
            MaterialDialog.Builder dialog = new MaterialDialog.Builder(mContext)
                    .title("您确定要退出么？")
                    .content("本次任务还存在未传输完成的文件，直接退出会导致传输中断！")
                    .negativeText("退出")
                    .positiveText("继续传输")
                    .onPositive((dialog1, which) -> {
                        dialog1.dismiss();
                    }).onNegative((dialog2, which) -> {
                        super.onBackPressed();
                    });
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        ApManager.closeAp(mContext);
        ReceiverManager.getInstance(mContext).release();
        super.onDestroy();
    }
}