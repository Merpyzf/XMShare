package com.merpyzf.transfermanager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.merpyzf.common.utils.CloseUtils;
import com.merpyzf.common.utils.NetworkUtil;
import com.merpyzf.common.utils.PersonalSettingUtils;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.SignMessage;
import com.merpyzf.common.helper.TimerHelper;
import com.merpyzf.common.helper.Timeout;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author wangke
 * @date 2017/12/3
 * 负责局域网内设备间UDP通讯
 */

public class PeerCommunicate extends Thread {
    private DatagramSocket mUdpSocket;
    private boolean running = true;
    private Context mContext;
    private Handler mHandler;
    private String mNickName;
    private int mAvatar;
    private static final String TAG = PeerCommunicate.class.getName();

    public PeerCommunicate(Context context, Handler handler, int port) {
        mContext = context;
        mHandler = handler;
        mNickName = PersonalSettingUtils.getNickname(context);
        mAvatar = PersonalSettingUtils.getAvatar(context);
        init(port);
    }

    private void init(int port) {
        try {
            mUdpSocket = new DatagramSocket(null);
            mUdpSocket.setReuseAddress(true);
            mUdpSocket.bind(new InetSocketAddress(port));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    /**
     * 负责监听UDP消息
     */
    @Override
    public void run() {
        try {
            if (mUdpSocket != null) {
                while (running) {
                    byte[] buffer = new byte[Const.UDP_BUFFER_LENGTH];
                    DatagramPacket receivePacket = new DatagramPacket(buffer, 0, buffer.length);
                    mUdpSocket.receive(receivePacket);
                    if (receivePacket.getLength() == 0) {
                        continue;
                    }
                    String hostAddress = receivePacket.getAddress().getHostAddress();
                    String receiveMsg = new String(buffer, 0, receivePacket.getLength(), "utf-8");
                    // 当接收到UDP消息不是来自本机才进行一下操作
                    if (!msgIsFormMe(hostAddress)) {
                        Log.i("wk", "收到的UDP消息: " + receiveMsg);
                        SignMessage signMessage = SignMessage.decodeProtocol(receiveMsg);
                        if (signMessage != null) {
                            Message message = Message.obtain();
                            message.obj = signMessage;
                            // 将收到的消息转发给主线程处理
                            mHandler.sendMessage(message);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            running = false;
        } finally {
            if (null != mUdpSocket) {
                mUdpSocket.close();
            }
        }

    }

    /**
     * 发送UDP数据
     *
     * @param msg  消息内容
     * @param dest 目标地址
     * @param port 端口号
     */
    public synchronized void sendUdpData(String msg, InetAddress dest, int port) {

        try {
            if (mUdpSocket != null) {
                byte[] buffer = msg.getBytes(Const.S_CHARSET);
                DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, dest, port);
                Log.i("wk", "request conn-> " + dest.getHostAddress() + ": " + port + "msg-> " + msg);
                mUdpSocket.send(sendPacket);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            CloseUtils.close(mUdpSocket);
        }
    }

    /**
     * 发送广播信息
     */
    public void sendBroadcast(SignMessage signMessage) {
        InetAddress broadcastAddress = null;
        try {
            broadcastAddress = NetworkUtil.getBroadcastAddress(mContext);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String sinMsg = signMessage.convertProtocolStr();
        sendUdpData(sinMsg, broadcastAddress, Const.UDP_PORT);
    }

    /**
     * 发送广播信息
     */
    public void sendBroadcast(String dest, SignMessage signMessage) {
        InetAddress broadcastAddress = null;
        try {
            broadcastAddress = InetAddress.getByName(dest);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String sinMsg = signMessage.convertProtocolStr();
        sendUdpData(sinMsg, broadcastAddress, Const.UDP_PORT);
    }


    /**
     * 将当前设备的信息回复给对端
     */
    public void replyMsg() {
        SignMessage signMessage = new SignMessage();
        signMessage.setHostAddress(NetworkUtil.getLocalIp(mContext));
        signMessage.setCmd(SignMessage.CMD.ON_LINE);
        signMessage.setNickName(mNickName);
        signMessage.setAvatarPosition(mAvatar);
        sendBroadcast(signMessage);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mUdpSocket != null) {
            TimerHelper timerHelper = new TimerHelper(null, new Timeout() {
                @Override
                public void onTimeOut() {
                    running = false;
                    mUdpSocket.close();
                }
            }, 0, false);
            timerHelper.start();
        }
    }

    /**
     * 判断收到的消息是否来自自己
     *
     * @param hostAddress
     * @return
     */
    public boolean msgIsFormMe(String hostAddress) {
        return NetworkUtil.isLocal(hostAddress);
    }
}
