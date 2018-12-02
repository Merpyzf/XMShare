package com.merpyzf.fileserver;

import android.content.Context;
import android.util.Log;

import com.merpyzf.fileserver.common.Const;
import com.merpyzf.fileserver.common.bean.FileInfo;
import com.merpyzf.fileserver.handler.DownloadRequestHandler;
import com.merpyzf.fileserver.handler.ThumbRequestHandler;
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
    // TODO: 2018/12/1 文件共享服务器关闭后注意要清理assets文件夹存放的临时文件 
    private static FileServer sFileServer;
    private Server mServer;

    private FileServer() {
    }


    public static FileServer getInstance() {
        if (sFileServer == null) {
            synchronized (FileServer.class) {
                if (sFileServer == null) {
                    sFileServer = new FileServer();
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
    public void startupFileShareServer(Context context, String hostAddress, List<FileInfo> fileList) {

        try {
            mServer = AndServer.serverBuilder()
                    .port(Const.DEFAULT_PORT)
                    .inetAddress(InetAddress.getByName(hostAddress))
                    .registerHandler(Const.URL_HOME, new WebUriHandler(context, fileList))
                    .registerHandler(Const.URL_ICO, new ThumbRequestHandler(context, fileList))
                    .registerHandler(Const.URL_DOWN, new DownloadRequestHandler(fileList))
                    .build();
            Log.i("ww2k", "文件共享服务开启了");
            mServer.startup();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启文件共享服务，用于分享设备的整个目录
     */
    public void startupFileServer(Context context, String hostAddress) {

        //try {
        //    mServer = AndServer.serverBuilder()
        //            .port(Const.DEFAULT_PORT)
        //            .inetAddress(InetAddress.getByName(hostAddress))
        //            .registerHandler("/", new WebUriHandler(context))
        //            .registerHandler("/img", new ThumbRequestHandler())
        //            .registerHandler("/file", new DownloadRequestHandler())
        //            .build();
        //    mServer.startup();
        //} catch (UnknownHostException e) {
        //    e.printStackTrace();
        //}

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
        if (mServer != null) {
            if (mServer.isRunning()) {
                mServer.shutdown();
            }
        }
    }


}
