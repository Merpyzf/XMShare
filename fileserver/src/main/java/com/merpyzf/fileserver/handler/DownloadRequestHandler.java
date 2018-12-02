package com.merpyzf.fileserver.handler;

import com.merpyzf.fileserver.common.bean.FileInfo;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.SimpleRequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;
import com.yanzhenjie.andserver.view.View;

import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.FileEntity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * description: 处理文件下载请求(包含所有类型的文件)
 * author: wangke
 * date: 2018/8/11.
 * version: 1.0
 *
 * @author wangke
 */
public class DownloadRequestHandler extends SimpleRequestHandler {
    private static final String TAG = RequestHandler.class.getSimpleName();
    private List<FileInfo> mFileList;

    public DownloadRequestHandler(List<FileInfo> fileList) {
        this.mFileList = fileList;
    }

    @Override
    protected View handle(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = HttpRequestParser.parseParams(request);
        String fileName = params.get("name");
        FileInfo fileInfo = getFileInfo(fileName);
        View view = null;
        if (fileInfo == null) {
            view = new View(404);
        } else {
            File file = new File(fileInfo.getPath());
            FileEntity fileEntity = new FileEntity(file);
            view = new View(200, fileEntity);
            if (fileInfo.getType() != FileInfo.FILE_TYPE_STORAGE) {
                view.addHeader("Content-Disposition", "attachment;filename=" + fileName + "." + fileInfo.getSuffix());
            } else {
                view.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            }
            view.addHeader("Pragma", "No-cache");
            view.addHeader("Cache-Control", "No-cache");
            view.addHeader("Expires", "0");
        }
        return view;
    }

    public FileInfo getFileInfo(String fileName) {
        FileInfo fileInfo = null;
        if (mFileList == null || mFileList.size() == 0) {
            return null;
        }
        for (FileInfo f : mFileList) {
            if (fileName.equals(f.getName())) {
                fileInfo = f;
            }
        }
        return fileInfo;
    }
}
