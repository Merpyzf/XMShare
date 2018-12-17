package com.merpyzf.fileserver.handler;

import android.content.Context;
import android.graphics.Bitmap;

import com.merpyzf.common.utils.IOUtils;
import com.merpyzf.fileserver.common.Const;
import com.merpyzf.fileserver.common.bean.FileInfo;
import com.merpyzf.common.utils.BitmapUtils;
import com.merpyzf.fileserver.util.FileTypeHelper;
import com.merpyzf.common.utils.FileUtils;
import com.merpyzf.common.utils.FilePathManager;
import com.yanzhenjie.andserver.SimpleRequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;
import com.yanzhenjie.andserver.view.View;

import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.FileEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * description: 处理图片相关的url请求
 * author: wangke
 * date: 2018/8/11.
 * version: 1.0
 *
 * @author wangke
 */
public class ThumbRequestHandler extends SimpleRequestHandler {
    private static final String TAG = ThumbRequestHandler.class.getSimpleName();
    private Context mContext = null;
    private List<FileInfo> mFileList;

    public ThumbRequestHandler(Context context, List<FileInfo> fileList) {
        this.mContext = context;
        this.mFileList = fileList;
    }


    @Override
    protected View handle(HttpRequest request, HttpResponse response) {
        View view = null;
        try {
            Map<String, String> params = HttpRequestParser.parseParams(request);
            int type = Integer.parseInt(params.get("type"));
            String suffix = params.get("suffix");
            String fileName = params.get("filename");
            FileEntity thumbFileEntity = getThumbFileEntity(fileName, suffix, type);
            if (thumbFileEntity == null) {
                view = new View(404);
            } else {
                view = new View(200, thumbFileEntity);
                view.addHeader("Content-Type", "image/*");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

    public FileEntity getThumbFileEntity(String fileName, String suffix, int type) {
        FileEntity fileEntity = null;
        String thumbFilePath;
        switch (type) {
            // 网站图标资源
            case Const.WEB_SITE_ICO:
                // 如果要取assent目录下的文件，需要先拷贝一份到本地才能使用
                InputStream inputStream = FileUtils.OpenFileFromAssets(mContext, "img/" + fileName);
                // 获取临时存放资源文件的目录
                File tempAssetsDir = FilePathManager.getAssentImgPath();
                File thumbFile = new File(tempAssetsDir, fileName);
                // 将从assets目录下读取的文件拷贝到设备目录中去，以供使用
                IOUtils.writeStreamToFile(inputStream, thumbFile);
                fileEntity = new FileEntity(thumbFile);
                break;

            case FileInfo.FILE_TYPE_APP:
                thumbFilePath = FilePathManager.getLocalAppThumbCacheFile(fileName).getPath();
                if (thumbFilePath != null) {
                    File file = new File(thumbFilePath);
                    fileEntity = new FileEntity(file);
                }

                break;
            case FileInfo.FILE_TYPE_IMAGE:
                // 压缩图片，然后获取压缩后的图片路径
                FileInfo imageFileInfo = getImageFileByName(fileName);
                File imageFile = new File(imageFileInfo.getPath());
                if (imageFile != null) {
                    if ("gif".equals(suffix)) {
                        fileEntity = new FileEntity(imageFile);
                    } else {
                        // 压缩图片
                        Bitmap bitmap = BitmapUtils.getCustomSizeImage(imageFile.getPath(), 200f, 200f, 50);
                        File compressImgFile = new File(FilePathManager.getAssentImgPath(), fileName);
                        BitmapUtils.writeBitmapToFile(bitmap, compressImgFile);
                        fileEntity = new FileEntity(compressImgFile);
                    }
                }
                break;
            case FileInfo.FILE_TYPE_MUSIC:
                // 通过专辑id来获取专辑封面
                thumbFilePath = FilePathManager.getLocalMusicAlbumCacheFile(fileName).getPath();
                if (thumbFilePath != null) {
                    File file = new File(thumbFilePath);
                    fileEntity = new FileEntity(file);
                }
                break;
            case FileInfo.FILE_TYPE_VIDEO:
                thumbFilePath = FilePathManager.getLocalVideoThumbCacheFile(fileName).getPath();
                if (thumbFilePath != null) {
                    File file = new File(thumbFilePath);
                    fileEntity = new FileEntity(file);
                }
                break;
            case FileInfo.FILE_TYPE_COMPACT:
                //strFileType = "压缩文件";
                break;
            case FileInfo.FILE_TYPE_DOCUMENT:
                //strFileType = "文档";
                break;
            //    设备内文件,文件中包含后缀
            case FileInfo.FILE_TYPE_STORAGE:
                boolean photoType = FileTypeHelper.isPhotoType(suffix);
                if (photoType) {

                    FileInfo f = getImageFileByName(fileName);
                    if (f != null) {
                        File image = new File(f.getPath());
                        if ("gif".equals(suffix)) {
                            fileEntity = new FileEntity(image);
                        } else {
                            // 压缩图片
                            Bitmap bitmap = BitmapUtils.getCustomSizeImage(image.getPath(), 200f, 200f, 50);
                            File compressImgFile = new File(FilePathManager.getAssentImgPath(), fileName);
                            BitmapUtils.writeBitmapToFile(bitmap, compressImgFile);
                            fileEntity = new FileEntity(compressImgFile);
                        }
                    }

                } else {
                    File fileTypeThumb = FileTypeHelper.getFileTypeThumbBySuffix(mContext, suffix);
                    fileEntity = new FileEntity(fileTypeThumb);
                }
                break;
            default:
                break;

        }

        return fileEntity;
    }

    public FileInfo getImageFileByName(String fileName) {
        for (FileInfo fileInfo : mFileList) {
            if (fileInfo.getName().equals(fileName)) {
                return fileInfo;
            }
        }
        return null;
    }

}
