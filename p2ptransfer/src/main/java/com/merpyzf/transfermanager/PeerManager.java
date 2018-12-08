package com.merpyzf.transfermanager;

import android.content.Context;

import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.SignMessage;
import com.merpyzf.transfermanager.interfaces.OnPeerActionListener;
import com.merpyzf.transfermanager.interfaces.PeerTransferBreakCallBack;
import com.merpyzf.transfermanager.util.NetworkUtil;
import com.merpyzf.transfermanager.util.PersonalSettingHelper;
import com.merpyzf.transfermanager.util.SharedPreUtils;
import com.merpyzf.transfermanager.util.WifiHelper;
import com.merpyzf.transfermanager.util.timer.OSTimer;
import com.merpyzf.transfermanager.util.timer.Timeout;

import java.net.InetAddress;

/**
 * @author wangke
 * @date 2017/12/17
 */

public class PeerManager {

    private Context mContext = null;
    private PeerHandler mPeerHandler;
    private PeerCommunicate mPeerCommunicate;
    private PeerTransferBreakCallBack mTransferBreakCallback = null;
    private OSTimer mOnLineTimer;
    private boolean isStop = false;
    private String mNickName;
    private int mAvatar;


    public PeerManager(Context context) {
        this.mContext = context;
        this.mPeerHandler = new PeerHandler(mContext);
        this.mPeerCommunicate = new PeerCommunicate(mContext, mPeerHandler, Const.UDP_PORT);
        this.mNickName = PersonalSettingHelper.getNickname(context);
        this.mAvatar = PersonalSettingHelper.getAvatar(context);
    }


    /**
     * 发送传输中断广播
     */
    public void sendTransferBreakMsg() {


        Timeout timeout = new Timeout() {
            @Override
            public void onTimeOut() {
                // TODO: 2018/4/26 替换掉这种线程操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String name = Thread.currentThread().getName();
                        SignMessage signMessage = new SignMessage();
                        signMessage.setHostAddress(NetworkUtil.getLocalIp(mContext));
                        signMessage.setMsgContent("TRANSFER_BREAK");
                        signMessage.setCmd(SignMessage.Cmd.TRANSFER_BREAK);
                        signMessage.setNickName(mNickName);
                        signMessage.setAvatarPosition(mAvatar);
                        sendBroadcastMsg(signMessage);
                    }
                }).start();
            }
        };
        timeout.onTimeOut();
        //发送两个广播消息
        new OSTimer(null, timeout, 50, false).start();
        new OSTimer(null, timeout, 100, false).start();
    }

    /**
     * 发送设备上线广播
     */
    public OSTimer sendOnLineBroadcast(boolean isCycle) {
        Timeout timeout = new Timeout() {
            @Override
            public void onTimeOut() {
                // TODO: 2018/4/26 替换掉这种线程操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String name = Thread.currentThread().getName();
                        SignMessage signMessage = new SignMessage();
                        signMessage.setHostAddress(NetworkUtil.getLocalIp(mContext));
                        signMessage.setMsgContent("ON_LINE");
                        signMessage.setCmd(SignMessage.Cmd.ON_LINE);
                        signMessage.setNickName(mNickName);
                        signMessage.setAvatarPosition(mAvatar);
                        sendBroadcastMsg(signMessage);

                    }
                }).start();
            }
        };

        timeout.onTimeOut();
        //发送两个广播消息
        OSTimer osTimer = new OSTimer(null, timeout, 100, isCycle);
        osTimer.start();
        return osTimer;
    }


    /**
     * 发送设备上线广播
     */
    public OSTimer sendOnLineBroadcast(final String destAddress, boolean isCycle) {
        Timeout timeout = new Timeout() {
            @Override
            public void onTimeOut() {
                // TODO: 2018/4/26 替换掉这种线程操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int count = 0;
                        while (!NetworkUtil.pingIpAddress(destAddress) && count < Const.PING_COUNT) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            count += 1;
                        }
                        String name = Thread.currentThread().getName();
                        SignMessage signMessage = new SignMessage();
                        signMessage.setHostAddress(WifiHelper.getInstance(mContext).getHotspotLocalIpAddress());
                        signMessage.setMsgContent("ON_LINE");
                        signMessage.setCmd(SignMessage.Cmd.ON_LINE);
                        signMessage.setNickName(mNickName);
                        signMessage.setAvatarPosition(mAvatar);
                        sendBroadcastMsg(destAddress, signMessage);
                    }
                }).start();
            }
        };

        timeout.onTimeOut();
        //发送两个广播消息
        OSTimer osTimer = new OSTimer(null, timeout, 100, isCycle);
        osTimer.start();
        return osTimer;
    }


    /**
     * 发送设备下线广播
     */
    public void sendOffLineBroadcast() {
        Timeout timeout = new Timeout() {
            @Override
            public void onTimeOut() {
                // TODO: 2018/4/26 替换掉这种线程操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SignMessage signMessage = new SignMessage();
                        signMessage.setHostAddress(NetworkUtil.getLocalIp(mContext));
                        signMessage.setMsgContent("OFF_LINE");
                        signMessage.setCmd(SignMessage.Cmd.OFF_LINE);
                        signMessage.setNickName(mNickName);
                        signMessage.setAvatarPosition(mAvatar);
                        sendBroadcastMsg(signMessage);
                    }
                }).start();
            }
        };
        timeout.onTimeOut();
        //发送两个广播消息
        new OSTimer(null, timeout, 50, false).start();
        new OSTimer(null, timeout, 100, false).start();
    }

    /**
     * 开启广播监听
     */
    public void startMsgListener() {
        if (mPeerCommunicate != null) {
            mPeerCommunicate.start();
        }
    }

    /**
     * 停止广播监听
     */
    public void stopMsgListener() {
        mPeerCommunicate.release();
    }

    /**
     * 给局域网内的其他设备发送UDP
     */
    public void send2Peer(final String msg, final InetAddress dest, final int port) {
        // TODO: 2018/4/26 替换掉这种线程操作
        // TODO: 2018/1/13 只发送一个UDP数据包，可能会出现UDP包丢失未能正确传送给对端的问题
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPeerCommunicate.sendUdpData(msg, dest, port);
            }
        }).start();


    }

    /**
     * 发送组播消息
     *
     * @param signMessage
     */
    public void sendBroadcastMsg(SignMessage signMessage) {
        mPeerCommunicate.sendBroadcast(signMessage);
    }

    public void sendBroadcastMsg(String dest, SignMessage signMessage) {
        mPeerCommunicate.sendBroadcast(dest, signMessage);
    }

    public void setPeerTransferBreakListener(PeerTransferBreakCallBack transferBreakListener) {
        mPeerHandler.setTransferBreakListener(transferBreakListener);
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

