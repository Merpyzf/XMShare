package com.merpyzf.fileserver.handler;

import android.content.Context;

import com.merpyzf.fileserver.common.Const;
import com.merpyzf.fileserver.common.bean.FileInfo;
import com.merpyzf.fileserver.util.IOUtils;
import com.merpyzf.transfermanager.util.SharedPreUtils;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
                //Log.i("wk", "path--> " + params.get("path"));
            } else {
                //Log.i("wk", "根路径");
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
        String logoSrcLink = Const.URL_ICO + "?type=" + Const.WEB_SITE_ICO + "&filename=logo.png";
        String followMeSrcLink = Const.URL_ICO + "?type=" + Const.WEB_SITE_ICO + "&filename=follow_me.png";
        String indexHtml = indexTemplate.replace("{title}", "小马快传文件服务")
                .replace("{nav_title}", "小马快传 分享文件个数: " + mFileList.size())
                .replace("{info}", "共享自: " + SharedPreUtils.getNickName(mContext))
                .replace("{container}", containerHtml)
                .replace("{img_logo}", logoSrcLink)
                .replace("{img_fork_me}", followMeSrcLink);
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
        String fileName = fileInfo.getName();
        String suffix = fileInfo.getSuffix();
        String strFileType = null;
        String coverHref;

        switch (type) {
            case FileInfo.FILE_TYPE_APP:
                strFileType = "应用";
                break;
            case FileInfo.FILE_TYPE_IMAGE:
                strFileType = "图片";
                break;
            case FileInfo.FILE_TYPE_MUSIC:
                strFileType = "音乐";
                fileName = String.valueOf(fileInfo.getAlbumId());
                break;
            case FileInfo.FILE_TYPE_VIDEO:
                strFileType = "视频";
                break;
            case FileInfo.FILE_TYPE_COMPACT:
                strFileType = "压缩文件";
                break;
            case FileInfo.FILE_TYPE_DOCUMENT:
                strFileType = "文档";
                break;
            case FileInfo.FILE_TYPE_STORAGE:
                strFileType = "本地存储";
            default:
                break;

        }
        String[] sizeArrayStr = com.merpyzf.transfermanager.util.FileUtils.getFileSizeArrayStr(fileInfo.getLength());
        coverHref = Const.URL_ICO + "?type=" + type + "&filename=" + fileName + "&suffix=" + suffix;
        strFileItem = fileItemTemplate.replace("{file_href}", Const.URL_DOWN + "?name=" + fileInfo.getName())
                .replace("{file_name}", fileInfo.getName())
                .replace("{cover}", coverHref)
                .replace("{type}",  strFileType)
                .replace("{file_path}", " 路径: " + fileInfo.getPath())
                .replace("{file_size}", " 大小: " + sizeArrayStr[0] + sizeArrayStr[1]);
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
