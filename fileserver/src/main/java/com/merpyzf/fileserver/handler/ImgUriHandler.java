package com.merpyzf.fileserver.handler;

import android.util.Log;

import com.merpyzf.fileserver.common.bean.FileInfo;
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
 * description: 处理图片相关的url请求
 * author: wangke
 * date: 2018/8/11.
 * version: 1.0
 */
public class ImgUriHandler extends SimpleRequestHandler {
    private static final String TAG = ImgUriHandler.class.getSimpleName();

    @Override
    protected View handle(HttpRequest request, HttpResponse response) {
        View view = null;
        try {

            Map<String, String> params = HttpRequestParser.parseParams(request);
            int type = Integer.parseInt(params.get("type"));
            String path = params.get("path");
            if (type != FileInfo.FILE_TYPE_OTHER) {
                File file = new File(path);
                view = new View(200, new FileEntity(file));
                view.addHeader("Content-Disposition", "attachment;filename=" + file.getName());
            } else {
                //     根据文件的后缀名返回需要的文件封面的缩略图
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }
}
