package com.merpyzf.transfermanager;

import android.content.Context;
import android.util.Log;

import com.merpyzf.common.manager.ThreadPoolManager;
import com.merpyzf.common.utils.NetworkUtil;
import com.merpyzf.common.utils.PersonalSettingUtils;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.SignMessage;
import com.merpyzf.transfermanager.interfaces.OnPeerActionListener;
import com.merpyzf.transfermanager.interfaces.PeerTransferBreakCallBack;
import com.merpyzf.common.helper.WifiHelper;
import com.merpyzf.common.helper.TimerHelper;
import com.merpyzf.common.helper.Timeout;

import java.net.InetAddress;

/**
 * @author wangke
 * @date 2017/12/17
 */

public class PeerManager {

    private Context mContext;
    private PeerHandler mPeerHandler;
    private PeerCommunicate mPeerCommunicate;
    private String mNickName;
    private int mAvatar;


    public PeerManager(Context context) {
        this.mContext = context;
        this.mPeerHandler = new PeerHandler();
        this.mPeerCommunicate = new PeerCommunicate(mContext, mPeerHandler, Const.UDP_PORT);
        this.mNickName = PersonalSettingUtils.getNickname(context);
        this.mAvatar = PersonalSettingUtils.getAvatar(context);
    }


    /**
     * 发送传输中断广播
     */
    public void sendTransferBreakMsg() {
        Timeout timeout = new Timeout() {
            @Override
            public void onTimeOut() {
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcastMsg(createSignMessage(SignMessage.CMD.TRANSFER_BREAK));
                    }
                });
            }
        };
        timeout.onTimeOut();
        new TimerHelper(null, timeout, 50, false).start();
        new TimerHelper(null, timeout, 100, false).start();
        new TimerHelper(null, timeout, 150, false).start();
    }

    /**
     * 发送设备上线广播
     */
    public TimerHelper sendOnLineBroadcast(boolean isCycle) {
        Timeout timeout = new Timeout() {
            @Override
            public void onTimeOut() {
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcastMsg(createSignMessage(SignMessage.CMD.ON_LINE));
                    }
                });
            }
        };

        timeout.onTimeOut();
        TimerHelper timerHelper = new TimerHelper(null, timeout, 100, isCycle);
        timerHelper.start();
        return timerHelper;
    }

    /**
     * 发送设备下线广播
     */
    public void sendOffLineBroadcast() {
        Timeout timeout = new Timeout() {
            @Override
            public void onTimeOut() {
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        SignMessage signMessage = createSignMessage(SignMessage.CMD.OFF_LINE);
                        sendBroadcastMsg(signMessage);

                    }
                });
            }
        };
        timeout.onTimeOut();
        //发送两个广播消息
        new TimerHelper(null, timeout, 0, false).start();
        new TimerHelper(null, timeout, 50, false).start();
        new TimerHelper(null, timeout, 70, false).start();
        new TimerHelper(null, timeout, 90, false).start();
    }

    /**
     * 开启广播消息监听
     */
    public void startMsgListener() {
        if (mPeerCommunicate != null) {
            mPeerCommunicate.start();
        }
    }

    /**
     * 关闭广播消息监听
     */
    public void stopMsgListener() {
        mPeerCommunicate.release();
    }

    /**
     * 给局域网内的其他设备发送UDP
     */
    public void sendMsgToPeer(final String msg, final InetAddress dest, final int port) {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                mPeerCommunicate.sendUdpData(msg, dest, port);
            }
        });
    }

    /**
     * 发送组播消息
     *
     * @param signMessage
     */
    public void sendBroadcastMsg(SignMessage signMessage) {
        mPeerCommunicate.sendBroadcast(signMessage);
    }

    /**
     * 创建signMessage对象
     *
     * @param cmd
     */
    private SignMessage createSignMessage(int cmd) {
        SignMessage signMessage = new SignMessage();
        String hostAddress = NetworkUtil.getLocalIp(mContext);
        signMessage.setHostAddress(hostAddress);
        signMessage.setCmd(cmd);
        signMessage.setNickName(mNickName);
        signMessage.setAvatarPosition(mAvatar);
        return signMessage;
    }

    /**
     * 设置局域网内用户动作的监听
     *
     * @param onPeerActionListener
     */
    public void setOnPeerActionListener(OnPeerActionListener onPeerActionListener) {
        if (mPeerHandler != null) {
            mPeerHandler.setOnPeerActionListener(onPeerActionListener);
        }
    }


}

