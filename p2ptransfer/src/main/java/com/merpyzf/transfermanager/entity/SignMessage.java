package com.merpyzf.transfermanager.entity;

import com.merpyzf.transfermanager.common.Const;

/**
 * Created by wangke on 2017/12/12.
 * 连接建立前的示意报文
 */

public class SignMessage {

    // 数据包的包名
    private String packetName;
    // 发送数据包的设备的主机地址
    private String hostAddress;
    //消息的内容
    private String msgContent;
    //发送设备的昵称
    private String nickName;

    // 头像下标
    private int avatarPosition;

    // 这条消息对应的命令代号
    private int cmd;

    /**
     * 1. 上线/下线
     * 2. 配对请求(在界面上显示申请配对的那个用户)
     * 3. 回复配对请求
     */

    public static class cmd {

        //局域网内设备请求上线(在屏幕上显示局域网内可见的设备)
        public static final int ON_LINE = 1;
        // 回应请求建立连接的请求
        public static final int ANSWER_REQUEST_CONN = 2;
        //下线
        public static final int OFF_LINE = 3;
        //请求建立连接
        public static final int REQUEST_CONN = 4;
        // 文件传输中断的信号
        public static final int TRANSFER_BREAK = 5;


    }

    public SignMessage() {

        this.packetName = getTime();

    }


    public SignMessage(String hostAddress, String msgContent, String nickName) {
        this.packetName = getTime();
        this.hostAddress = hostAddress;
        this.msgContent = msgContent;
        this.nickName = nickName;
    }


    public String getPacketName() {
        return packetName;
    }

    public void setPacketName(String packetName) {
        this.packetName = packetName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
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
        protocolStr.append(packetName);
        protocolStr.append(Const.S_SEPARATOR);
        protocolStr.append(hostAddress);
        protocolStr.append(Const.S_SEPARATOR);
        protocolStr.append(nickName);
        protocolStr.append(Const.S_SEPARATOR);
        protocolStr.append(avatarPosition);
        protocolStr.append(Const.S_SEPARATOR);
        protocolStr.append(msgContent);
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
                message.setPacketName(msgProperties[0]);
                message.setHostAddress(msgProperties[1]);
                message.setNickName(msgProperties[2]);
                message.setAvatarPosition(Integer.valueOf(msgProperties[3]));
                message.setMsgContent(msgProperties[4]);
                message.setCmd(Integer.valueOf(msgProperties[5]));
            }
            return message;

        }


        return null;

    }


    public String getTime() {
        return String.valueOf(System.currentTimeMillis());
    }


}
