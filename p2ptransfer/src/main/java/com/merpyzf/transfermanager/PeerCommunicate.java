package com.merpyzf.transfermanager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.SignMessage;
import com.merpyzf.transfermanager.util.NetworkUtil;
import com.merpyzf.transfermanager.util.SharedPreUtils;
import com.merpyzf.transfermanager.util.timer.OSTimer;
import com.merpyzf.transfermanager.util.timer.Timeout;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author wangke
 * @date 2017/12/3
 * 负责局域网内设备间UDP通讯
 */

public class PeerCommunicate extends Thread {
    private DatagramSocket mUdpSocket;
    private boolean isLoop = true;
    private Context mContext = null;
    private Handler mHandler = null;
    private static final String TAG = PeerCommunicate.class.getName();

    public PeerCommunicate(Context context, Handler handler, int port) {
        mContext = context;
        mHandler = handler;
        String nickName = SharedPreUtils.getString(context, Const.SP_USER, "nickName", "");
        init(port);
    }

    public PeerCommunicate(int port) {
        init(port);
    }

    private void init(int port) {
        try {
            // 初始化发送端所用socket
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
                while (isLoop) {
                    byte[] buffer = new byte[Const.BUFFER_LENGTH];
                    DatagramPacket receivePacket = new DatagramPacket(buffer, 0, Const.BUFFER_LENGTH);
                    //block
                    mUdpSocket.receive(receivePacket);
                    if (receivePacket.getLength() == 0) {
                        continue;
                    }
                    String hostAddress = receivePacket.getAddress().getHostAddress();
                    String receiveMsg = new String(buffer, 0, buffer.length, "utf-8");
                    // 当接收到UDP消息不是来自本机才进行一下操作
                    if (!NetworkUtil.isLocal(hostAddress)) {
                        SignMessage signMessage = SignMessage.decodeProtocol(receiveMsg);
                        Message message = Message.obtain();
                        message.obj = signMessage;
                        // 将收到的消息转发给主线程处理
                        mHandler.sendMessage(message);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            isLoop = false;
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
                mUdpSocket.send(sendPacket);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            mUdpSocket.close();
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
        String name = Thread.currentThread().getName();
        SignMessage signMessage = new SignMessage();
        signMessage.setHostAddress(NetworkUtil.getLocalIp(mContext));
        signMessage.setMsgContent("ON_LINE");
        signMessage.setCmd(SignMessage.Cmd.ON_LINE);
        signMessage.setNickName(SharedPreUtils.getNickName(mContext));
        signMessage.setAvatarPosition(SharedPreUtils.getAvatar(mContext));
        sendBroadcast(signMessage);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mUdpSocket != null) {
            OSTimer osTimer = new OSTimer(null, new Timeout() {
                @Override
                public void onTimeOut() {
                    isLoop = false;
                    mUdpSocket.close();
                }
            }, 0, false);
            osTimer.start();
        }
    }
}
