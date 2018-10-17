package com.merpyzf.fileserver;

import android.content.Context;

import com.merpyzf.fileserver.common.Const;
import com.merpyzf.fileserver.common.bean.FileInfo;
import com.merpyzf.fileserver.handler.FileUriHandler;
import com.merpyzf.fileserver.handler.ImgUriHandler;
import com.merpyzf.fileserver.handler.WebUriHandler;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * description: 文件服务开启关闭的主控制类
 * author: wangke
 * date: 2018/8/11.
 * version:1.0
 */
public class FileServer {

    private static FileServer sFileServer;
    private int port = Const.DEFAULT_PORT;
    private String host;
    private Server mServer;

    private FileServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 单例获取一个FileServer对象，需要指定访问此服务的端口号
     *
     * @param port 端口号
     * @return FileServer对象
     */
    public static FileServer getInstance(String host, int port) {
        if (sFileServer == null) {
            synchronized (FileServer.class) {
                if (sFileServer == null) {
                    sFileServer = new FileServer(host, port);
                }
            }
        }
        return sFileServer;
    }


    /**
     * 开启文件共享服务，用于分享指定文件
     *
     * @param fileList 待分享文件集合
     */
    public void startupFileShareServer(Context context, List<FileInfo> fileList) {

        try {
            mServer = AndServer.serverBuilder()
                    .port(port)
                    .inetAddress(InetAddress.getByName(host))
                    .registerHandler("/", new WebUriHandler(context, fileList))
                    .registerHandler("/img", new ImgUriHandler())
                    .registerHandler("/file", new FileUriHandler())
                    .build();
            mServer.startup();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启文件共享服务，用于分享设备的整个目录
     */
    public void startupFileServer(Context context) {

        try {
            mServer = AndServer.serverBuilder()
                    .port(port)
                    .inetAddress(InetAddress.getByName(host))
                    .registerHandler("/", new WebUriHandler(context))
                    .registerHandler("/img", new ImgUriHandler())
                    .registerHandler("/file", new FileUriHandler())
                    .build();
            mServer.startup();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断服务器当前状态
     *
     * @return true: 运行中 false:未运行
     */
    public boolean isRunning() {
        return mServer.isRunning();
    }

    /**
     * 停止服务
     */
    public void stopRunning() {
        mServer.shutdown();
    }

    /**
     * 设置绑定此服务的主机端口号，设置成功后需要重启服务才能生效
     *
     * @param port 端口号
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 设置绑定此服务的主机地址，设置成功后需要重启服务才能生效
     *
     * @param host 主机地址
     */
    public void setHost(String host) {
        this.host = host;
    }
}
