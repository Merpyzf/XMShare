package com.merpyzf.transfermanager.entity;

import com.merpyzf.transfermanager.common.Const;

/**
 * @author wangke
 * @date 2017/12/12
 * 连接建立前的示意报文
 */

public class SignMessage {
    /**
     * 发送数据包的设备的主机地址
     */
    private String hostAddress;
    /**
     * 发送设备的昵称
     */
    private String nickName;
    /**
     * 头像
     */
    private int avatarPosition;
    /**
     * 命令
     */
    private int cmd;

    /**
     * 1. 上线/下线
     * 2. 配对请求(在界面上显示申请配对的那个用户)
     * 3. 回复配对请求
     */

    public static class CMD {

        /**
         * 局域网内设备请求上线(在屏幕上显示局域网内可见的设备)
         */
        public static final int ON_LINE = 1;
        /**
         * 回应请求建立连接的请求
         */
        public static final int ANSWER_REQUEST_CONN = 2;
        /**
         * 下线
         */
        public static final int OFF_LINE = 3;
        /**
         * 请求建立连接
         */
        public static final int REQUEST_CONN = 4;
        /**
         * 文件传输中断的信号
         */
        public static final int TRANSFER_BREAK = 5;
    }

    public SignMessage(String hostAddress, int avatar, String nickName, int cmd) {
        this.hostAddress = hostAddress;
        this.nickName = nickName;
        this.avatarPosition = avatar;
        this.cmd = cmd;
    }


    public SignMessage() {
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getAvatarPosition() {
        return avatarPosition;
    }

    public void setAvatarPosition(int avatarPosition) {
        this.avatarPosition = avatarPosition;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    /**
     * 将对象转换成协议字符
     *
     * @return
     */
    public String convertProtocolStr() {
        StringBuilder protocolStr = new StringBuilder();
        protocolStr.append(hostAddress);
        protocolStr.append(Const.S_SEPARATOR);
        protocolStr.append(nickName);
        protocolStr.append(Const.S_SEPARATOR);
        protocolStr.append(avatarPosition);
        protocolStr.append(Const.S_SEPARATOR);
        protocolStr.append(String.valueOf(cmd));
        protocolStr.append(Const.S_END);
        return protocolStr.toString();
    }

    /**
     * 根据规则进行解码
     *
     * @param signMessage
     * @return
     */
    public static SignMessage decodeProtocol(String signMessage) {
        if (signMessage != null && signMessage.length() > 0) {
            int end = signMessage.indexOf(Const.S_END);
            signMessage = signMessage.subSequence(0, end).toString();
            SignMessage message = new SignMessage();
            String[] msgProperties = signMessage.split(Const.S_SEPARATOR);
            if (msgProperties.length == Const.MSG_PROPERTIES_LENGTH) {
                message.setHostAddress(msgProperties[0]);
                message.setNickName(msgProperties[1]);
                message.setAvatarPosition(Integer.valueOf(msgProperties[2]));
                message.setCmd(Integer.valueOf(msgProperties[3]));
            }
            return message;
        }
        return null;
    }
}
