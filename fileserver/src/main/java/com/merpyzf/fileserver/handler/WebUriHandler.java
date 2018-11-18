package com.merpyzf.fileserver.handler;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.merpyzf.fileserver.common.bean.FileInfo;
import com.merpyzf.fileserver.util.FileUtils;
import com.merpyzf.fileserver.util.IOUtils;
import com.merpyzf.fileserver.util.Md5Utils;
import com.merpyzf.transfermanager.util.FilePathManager;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.security.auth.login.LoginException;

/**
 * description: 用于根据请求的uri生成页面返回
 * author: wangke
 * date: 2018/8/11.
 * version:1.0
 */
public class WebUriHandler implements RequestHandler {
    private List<FileInfo> mFileList = null;
    private Context mContext = null;
    private static final String TAG = WebUriHandler.class.getSimpleName();

    public WebUriHandler(Context context, List<FileInfo> fileList) {
        //    在构造方法中构造html页面
        this.mFileList = fileList;
        this.mContext = context;

    }

    public WebUriHandler(Context context) {
        this.mContext = context;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

        String htmlPage;
        // 分享指定文件
        if (mFileList != null) {
            htmlPage = generateSharePage(mFileList);
            //  分享设备中的所有目录，根据用户的选择
        } else {
            Map<String, String> params = HttpRequestParser.parseParams(request);
            String currentDirPath = "";
            if (params.containsKey("path")) {
                Log.i("wk", "path--> " + params.get("path"));
            } else {
                Log.i("wk", "根路径");
            }
            htmlPage = generateSharePage(currentDirPath);
        }

        response.setEntity(new StringEntity(htmlPage, "utf-8"));
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        response.setStatusCode(200);

    }

    /**
     * 根据文件列表集合生成分享页面
     *
     * @param mFileList 文件集合
     */
    private String generateSharePage(List<FileInfo> mFileList) {

        String indexTemplate = getTemplateContent("index.html");

        StringBuilder containerHtml = new StringBuilder();
        for (FileInfo fileInfo : mFileList) {
            String strFileItem = generateFileItem(fileInfo);
            containerHtml.append(strFileItem);
        }
        String indexHtml = indexTemplate.replace("{title}", "小马快传文件服务")
                .replace("{nav_title}", "小马快传 分享文件个数: " + mFileList.size())
                .replace("{current_path}", "")
                .replace("{container}", containerHtml);
        return indexHtml;
    }

    /**
     * 根据FileInfo对象生成单个文件的Html代码
     *
     * @param fileInfo FileInfo对象
     * @return 生成的html代码
     */
    private String generateFileItem(FileInfo fileInfo) {

        String fileItemTemplate = getTemplateContent("file_item.template");
        StringBuilder strFileItemHtml = new StringBuilder();
        String strFileItem = null;
        // 获取文件类型
        int type = fileInfo.getType();
        String strFileType = null;
        String coverHref;
        String thumbPath = null;
        if (type == FileInfo.FILE_TYPE_OTHER) {

        } else {
            switch (type) {
                case FileInfo.FILE_TYPE_APP:
                    thumbPath = FilePathManager.getLocalAppThumbCacheFile(fileInfo.getName()).getPath();
                    strFileType = "应用apk";
                    break;
                case FileInfo.FILE_TYPE_IMAGE:
                    thumbPath = fileInfo.getPath();
                    strFileType = "图片";
                    String name = fileInfo.getName();
                    Log.i(TAG, "图片名称--> " + name);

                    break;
                case FileInfo.FILE_TYPE_MUSIC:
                    thumbPath = FilePathManager.getLocalMusicAlbumCacheFile(String.valueOf(fileInfo.getAlbumId())).getPath();
                    strFileType = "音乐";
                    break;
                case FileInfo.FILE_TYPE_VIDEO:
                    thumbPath = FilePathManager.getLocalVideoThumbCacheFile(fileInfo.getName()).getPath();
                    strFileType = "视频";
                    break;
                case FileInfo.FILE_TYPE_COMPACT:
                    strFileType = "压缩文件";
                    break;
                case FileInfo.FILE_TYPE_DOCUMENT:
                    strFileType = "文档";
                    break;
                default:
                    break;

            }
        }

        coverHref = "/img?type=" + type + "&path=" + thumbPath;
        strFileItem = fileItemTemplate.replace("{file_href}", "/file?path=" + fileInfo.getPath() + "&name=" + fileInfo.getName() + "." + fileInfo.getSuffix())
                .replace("{file_name}", fileInfo.getName())
                .replace("{cover}", coverHref)
                .replace("{type}", "类型: " + strFileType)
                .replace("{file_path}", "路径:" + fileInfo.getPath())
                .replace("{file_size}", "大小:" + String.valueOf(fileInfo.getLength()));
        return strFileItem;

    }

    /**
     * 获取Assent目录下模板文件的内容
     *
     * @param fileName 包含后缀的文件名
     */
    private String getTemplateContent(String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return IOUtils.stream2Str(inputStream);
    }

    /**
     * 根据根据路径生成分享页面
     *
     * @param currentDirPath
     */
    private String generateSharePage(String currentDirPath) {

        return "空";

    }
}
