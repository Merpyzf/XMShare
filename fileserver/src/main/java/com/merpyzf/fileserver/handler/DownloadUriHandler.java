package com.merpyzf.fileserver.handler;

import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.SimpleRequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;
import com.yanzhenjie.andserver.view.View;

import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.FileEntity;
import java.io.File;
import java.io.IOException;
import java.util.Map;
/**
 * description: 处理文件下载请求(包含所有类型的文件)
 * author: wangke
 * date: 2018/8/11.
 * version: 1.0
 */
public class DownloadUriHandler extends SimpleRequestHandler {
    private static final String TAG = RequestHandler.class.getSimpleName();

    @Override
    protected View handle(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = HttpRequestParser.parseParams(request);
        String filePath = params.get("path");
        String fileName = params.get("name");
        File file = new File(filePath);
        View view = new View(200, new FileEntity(file));
        view.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        view.addHeader("Pragma", "No-cache");
        view.addHeader("Cache-Control", "No-cache");
        view.addHeader("Expires", "0");
        return view;
    }
}
