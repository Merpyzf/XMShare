package com.merpyzf.xmshare.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.merpyzf.common.utils.PersonalSettingUtils;
import com.merpyzf.common.utils.ToastUtils;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.BaseFileInfo;
import com.merpyzf.transfermanager.entity.Peer;
import com.merpyzf.transfermanager.observer.AbsTransferObserver;
import com.merpyzf.transfermanager.send.SenderManager;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.base.BaseActivity;
import com.merpyzf.xmshare.ui.fragment.ScanPeerFragment;
import com.merpyzf.xmshare.ui.fragment.transfer.TransferSendFragment;
import com.merpyzf.xmshare.ui.interfaces.OnPairActionListener;

import net.qiujuer.genius.ui.animation.AnimatorListener;

import java.util.List;

import butterknife.BindView;

/**
 * 我要发送界面
 * <p>
 * <p>
 * 文件发送:
 * <p>
 * 发送端：
 * <p>
 * 发送端也需要建立一个UDPServer，用来获取 接收端的回应
 * <p>
 * 1.当收到对端的回复时，在界面上显示这个可连接的接收端
 * 2.点击接收端设备头像准备建立连接
 * 3.当发送端和接收端确认建立连接时，退出当前页面时发送离线广播，告知对端退出了
 * <p>
 * UDP包的几种请求状态:
 * <p>
 * 上线通知
 * 请求连接
 * 同意建立连接
 * 下线通知
 *
 * @author wangke
 */
public class SendActivity extends BaseActivity {

    @BindView(R.id.linear_rocket)
    LinearLayout mLinearRocket;
    @BindView(R.id.frame_content)
    FrameLayout mFrameContent;
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;

    private ScanPeerFragment mScanPeerFragment;
    private TransferSendFragment mTransferSendFragment;
    private boolean isTransfering = false;
    private static final String TAG = SendActivity.class.getSimpleName();

    public static void start(Context context) {
        context.startActivity(new Intent(context, SendActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_send;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        // 加载好友扫描的Fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mScanPeerFragment = new ScanPeerFragment();
        transaction.replace(R.id.frame_content, mScanPeerFragment);
        transaction.commit();
    }

    @Override
    protected void initToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("我要发送");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void doCreateEvent() {
        mScanPeerFragment.setOnPeerActionListener(new OnPairActionListener() {
            @Override
            public void onSendConnRequest() {
                super.onSendConnRequest();
                mLinearRocket.setVisibility(View.VISIBLE);
                mLinearRocket.setFocusable(true);
                mFrameContent.setFocusable(false);
                mFrameContent.setClickable(false);
            }

            /**
             * 设备下线
             * @param peer
             */
            @Override
            public void onOffline(Peer peer) {
                super.onOffline(peer);
                mLinearRocket.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPairSuccess(Peer peer) {
                ObjectAnimator animator = startRocketFlyAnimation();
                animator.addListener(new AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mLinearRocket.setVisibility(View.GONE);
                        isTransfering = true;
                        loadSendFrg(peer);
                    }
                });
                // 注册一个文件发送状态的监听
                SenderManager.getInstance(mContext).register(new FileTransferListener());
            }

            @Override
            public void onPeerPairFailed(Peer peer) {
                mLinearRocket.setVisibility(View.GONE);
            }

            @Override
            public void onStartTransfer(Peer peer, List<BaseFileInfo> fileInfoLis) {
                isTransfering = true;
                // 热点模式下的文件发送回调
                loadSendFrg(peer);
                SenderManager.getInstance(mContext).register(new FileTransferListener());
            }
        });
    }


    private void loadSendFrg(Peer peer) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mTransferSendFragment = new TransferSendFragment(peer);
        transaction.replace(R.id.frame_content, mTransferSendFragment);
        transaction.commit();
    }

    private ObjectAnimator startRocketFlyAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mLinearRocket, "translationY", 0.0f - 1000f);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(500);
        animator.start();
        return animator;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
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


}
