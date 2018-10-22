package com.merpyzf.xmshare.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.merpyzf.transfermanager.PeerManager;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.observer.AbsTransferObserver;
import com.merpyzf.transfermanager.receive.ReceiverManager;
import com.merpyzf.transfermanager.util.ApManager;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.ui.fragment.ReceivePeerFragment;
import com.merpyzf.xmshare.ui.fragment.transfer.TransferReceiveFragment;
import com.merpyzf.xmshare.util.SharedPreUtils;
import com.merpyzf.xmshare.util.SingleThreadPool;
import com.merpyzf.xmshare.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 接收端:
 * <p>
 * 1. 定时发送广播信息，用于发送端对接收端设备的发现
 * 2. 需要开启一个UDPServer,用来显示需要进行连接的发送端的设备，点击发送端设备头像以完成连接确认
 * 3. 当接收端退出的时候,需要发送一个离线广播
 */
public class ReceiveActivity extends AppCompatActivity {


    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    private Context mContext;
    private Unbinder mUnbinder;
    private ReceivePeerFragment mReceivePeerFragment;
    private TransferReceiveFragment mTransferReceiveFragment;
    private PeerManager mPeerManager;
    // 标记当前是否正在进行文件传输
    private boolean isTransfering = false;
    private static final String TAG = ReceiveActivity.class.getSimpleName();

    /**
     * 打开这个页面
     *
     * @param context
     */
    public static void start(Context context) {

        context.startActivity(new Intent(context, ReceiveActivity.class));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        this.mContext = this;

        init();
        initUI();
        initEvent();


    }

    /**
     * 初始化对象
     */
    private void init() {

        mPeerManager = new PeerManager(mContext, SharedPreUtils.getNickName(mContext));
        // 开启一个UDP消息的监听
        mPeerManager.startMsgListener();
    }


    /**
     * 初始化UI
     */
    private void initUI() {
        mUnbinder = ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("我要接收");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 跳转到扫描附近设备的界面
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mReceivePeerFragment = new ReceivePeerFragment();
        transaction.replace(R.id.frame_content, mReceivePeerFragment);
        transaction.commit();

    }

    /**
     * 初始化事件
     */
    private void initEvent() {

        if (mReceivePeerFragment != null) {
            // 局域网下的事件
            mReceivePeerFragment.setOnReceivePairActionListener(new ReceivePeerFragment.OnReceivePairActionListener() {
                @Override
                public void onRequestSendFileAction() {
                    // 收到对端请求发送文件的请求
                    // 开启一个Socket服务
                    ReceiverManager receiverManager = ReceiverManager.getInstance(mContext);
                    receiverManager.register( new AbsTransferObserver() {
                        // TODO: 2018/1/28 增加一个文件全部传输完毕的回调
                        @Override
                        public void onTransferStatus(FileInfo fileInfo) {
                            // 如果当前传输的是最后一个文件，并且传输成功后重置标记
                            if (fileInfo.getIsLast() == Const.IS_LAST && fileInfo.getFileTransferStatus() == Const.TransferStatus.TRANSFER_SUCCESS) {
                                isTransfering = false;
                            }
                        }
                        @Override
                        public void onTransferError(String error) {
                            ToastUtils.showShort(mContext, error);
                            finish();
                        }
                    });

                    SingleThreadPool.getSingleton().execute(receiverManager);
                    // 当接收到待传输的文件列表时，跳转到文件传输的界面
                    receiverManager.setOnTransferFileListListener(transferFileList -> {

                        Log.i("w2k", "同意对端发送文件");
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        mTransferReceiveFragment = new TransferReceiveFragment(transferFileList);
                        transaction.replace(R.id.frame_content, mTransferReceiveFragment);
                        transaction.commit();
                        isTransfering = true;


                    });


                }

                // 热点下的事件
                @Override
                public void onApEnableAction() {
                    ReceiverManager receiverManager = ReceiverManager.getInstance(mContext);
                    SingleThreadPool.getSingleton().execute(receiverManager);
                    // 监听待传输的文件列表是否发送成功
                    receiverManager.setOnTransferFileListListener(transferFileList -> {

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        mTransferReceiveFragment = new TransferReceiveFragment(transferFileList);
                        transaction.replace(R.id.frame_content, mTransferReceiveFragment);
                        transaction.commit();
                        isTransfering = true;

                    });
                }
            });
        }

        if (mPeerManager != null) {

            mPeerManager.setPeerTransferBreakListener(peer -> {

                Log.i(TAG, "收到中断的广播了");

                if (isTransfering) {
                    Toast.makeText(mContext, "对端 " + peer.getNickName() + "退出了，即将关闭", Toast.LENGTH_SHORT).show();
                    finish();
                }

            });
        }


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

    @Override
    public void onBackPressed() {
        // 当接收页面关闭时发送传输中断的广播
        mPeerManager.sendTransferBreakMsg();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        // 释放资源
        if (ApManager.isApOn(mContext)) {
            ApManager.turnOffAp(mContext);
        }
        if (mPeerManager != null) {
            mPeerManager.stopMsgListener();
        }
        ReceiverManager.getInstance(mContext).release();

        super.onDestroy();
    }
}