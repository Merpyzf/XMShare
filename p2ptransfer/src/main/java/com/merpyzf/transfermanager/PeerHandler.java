package com.merpyzf.transfermanager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.merpyzf.common.utils.ToastUtils;
import com.merpyzf.transfermanager.entity.Peer;
import com.merpyzf.transfermanager.entity.SignMessage;
import com.merpyzf.transfermanager.interfaces.OnPeerActionListener;
import com.merpyzf.transfermanager.interfaces.PeerTransferBreakCallBack;

/**
 *
 * @author wangke
 * @date 2017/12/16
 * 接收从子线程转发来的UDP消息，并进行消息内容的处理和回调分发
 */
public class PeerHandler extends Handler {
    private Context mContext = null;
    private OnPeerActionListener mOnPeerActionListener = null;

    public PeerHandler(OnPeerActionListener onPeerActionListener) {
        this.mOnPeerActionListener = onPeerActionListener;
    }

    public PeerHandler() {
    }

    public PeerHandler(Looper looper) {
        super(looper);
    }

    /**
     * 获取接收到的udp消息
     * <p>
     * 1.我在线中可连接 (将可连接的设备显示在界面上)
     * 2.请求连接 ()
     * 3.回复可以连接
     *
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        SignMessage signMessage = (SignMessage) msg.obj;
        int cmd = signMessage.getCmd();

        Peer peer = new Peer();
        peer.setHostAddress(signMessage.getHostAddress());
        peer.setNickName(signMessage.getNickName());
        peer.setAvatarPosition(signMessage.getAvatarPosition());

        // 将事件的处理拆分到不同的类中进行处理
        // 1. PeerManager处理设备的上线下线
        // 2. sendManager处理文件的发送
        // 3. receiveManager处理文件的接收

        switch (cmd) {
            // 设备上线
            case SignMessage.CMD.ON_LINE:
                if (mOnPeerActionListener != null) {
                    mOnPeerActionListener.onDeviceOnLine(peer);
                }
                break;
            // 设备下线
            case SignMessage.CMD.OFF_LINE:
                if (mOnPeerActionListener != null) {
                    Log.i("ww3k","收到离线广播了" );
                    mOnPeerActionListener.onDeviceOffLine(peer);
                }
                break;
            case SignMessage.CMD.REQUEST_CONN:
                Log.i("ww3k","收到收到请求建立连接的广播了" );
                if (mOnPeerActionListener != null) {
                    mOnPeerActionListener.onRequestConnect(peer);
                }
                break;
            case SignMessage.CMD.ANSWER_REQUEST_CONN:
                if (mOnPeerActionListener != null) {
                    Log.i("ww3k","收到回应了" );
                    mOnPeerActionListener.onAnswerRequestConnect(peer);
                }
                break;
            default:
                break;
        }
    }

    public void setOnPeerActionListener(OnPeerActionListener onPeerActionListener) {
        this.mOnPeerActionListener = onPeerActionListener;
    }
}




