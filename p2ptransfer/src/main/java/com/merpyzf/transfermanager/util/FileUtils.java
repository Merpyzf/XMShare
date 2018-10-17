package com.merpyzf.transfermanager.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.merpyzf.transfermanager.R;
import com.merpyzf.transfermanager.common.Const;
import com.merpyzf.transfermanager.entity.ApkFile;
import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.transfermanager.entity.MusicFile;
import com.merpyzf.transfermanager.entity.PicFile;
import com.merpyzf.transfermanager.entity.VideoFile;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * Created by wangke on 2018/1/16.
 * 与文件操作相关的操作
 */

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 根据文件路径获取文件的后缀名
     *
     * @param filePath
     * @return 当获取不到时返回 ""
     */
    public static String getFileSuffix(String filePath) {
        int index = filePath.indexOf('.');
        if (index == -1) {
            return "";
        } else {
            String suffix = filePath.substring(index + 1);
            return suffix;
        }

    }


    /**
     * 根据文件获取文件的后缀名
     *
     * @param
     * @return 当获取不到时返回 ""
     */
    public static String getFileSuffix(File file) {

        String filePath = file.getPath();

        int index = filePath.indexOf('.');
        if (index == -1) {
            return "";
        } else {
            String suffix = filePath.substring(index + 1);
            return suffix;
        }


    }


    /**
     * Bitmap 写入到SD卡
     *
     * @param bitmap
     * @param resPath
     * @return
     */
    public static boolean bitmapToSDCard(Bitmap bitmap, String resPath) {
        if (bitmap == null) {
            return false;
        }
        File resFile = new File(resPath);
        try {
            FileOutputStream fos = new FileOutputStream(resFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Bitmap转ByteArray
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    /**
     * Drawable转Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        //建立对应的Bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);

        return bitmap;
    }


    /**
     * 获取文件的缩略图，并以字节数组的形式返回
     *
     * @param file
     * @return
     */
    public static byte[] getFileThumbByteArray(Context context, FileInfo file) {
        byte[] FileThumbArray = new byte[0];
        if (file instanceof ApkFile) {
            ApkFile appFile = (ApkFile) file;
            // 将文件的缩略图转换成
            Bitmap appThumbBmp = FileUtils.drawableToBitmap(appFile.getApkDrawable());
            // 对缩略图进行压缩
            FileThumbArray = FileUtils.bitmapToByteArray(zoomImage(context, appThumbBmp));

        } else if (file instanceof MusicFile) {
            MusicFile musicFile = (MusicFile) file;
            File thumbFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), Md5Utils.getMd5(musicFile.getAlbumId() + ""));
            // 获取图片原始的bitmap
            Bitmap appThumbBmp = BitmapFactory.decodeFile(thumbFile.getPath());
            FileThumbArray = FileUtils.bitmapToByteArray(zoomImage(context, appThumbBmp));
        } else if (file instanceof VideoFile) {
            VideoFile videoFile = (VideoFile) file;
            File thumbFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), Md5Utils.getMd5(videoFile.getPath()));
            Bitmap appThumbBmp = BitmapFactory.decodeFile(thumbFile.getPath());
            FileThumbArray = FileUtils.bitmapToByteArray(zoomImage(context, appThumbBmp));
        }
        return FileThumbArray;
    }


    /**
     * 对bitmap进行缩放 100px
     *
     * @param bmp
     * @return
     */
    public static Bitmap zoomImage(Context mContext, Bitmap bmp) {
        if (bmp == null) {
            bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_thumb_empty);
        }

        // 获取这个图片的宽和高
        float width = bmp.getWidth();
        float height = bmp.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) Const.SEND_FILE_THUMB_SIZE) / width;
        float scaleHeight = ((float) Const.SEND_FILE_THUMB_SIZE) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * @param file
     * @param inputStream
     * @param length
     */
    public static void writeStream2SdCard(File file, InputStream inputStream, int length) {

        int totalSize = 0;

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));

            if (bos == null) {
                return;
            }

            byte[] buffer = new byte[Const.BUFFER_LENGTH];
            // 读取文件
            while (length > 0) {
                int readLength = 0;
                if (length > Const.BUFFER_LENGTH) {
                    readLength = inputStream.read(buffer, 0, Const.BUFFER_LENGTH);
                } else {
                    readLength = inputStream.read(buffer, 0, (int) length);
                }
                bos.write(buffer, 0, readLength);

                totalSize += readLength;
                length -= readLength;
            }

            System.out.println("写入到文件的字节数:" + totalSize);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bos.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 获取待写入的File对象，根据要保存的文件类型在不同的目录下创建
     *
     * @param fileInfo
     * @return
     */
    public static File getSaveFile(FileInfo fileInfo) {
        File parent;
        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            switch (fileInfo.getType()) {
                case FileInfo.FILE_TYPE_APP:
                    parent = new File(Environment.getExternalStorageDirectory(), Const.SAVE_APK_PATH);
                    if (!parent.exists()) {
                        boolean isCreated = parent.mkdirs();
                    }
                    file = new File(parent, FileUtils.removeFileSeparator(fileInfo.getName() + "." + fileInfo.getSuffix()));
                    break;
                case FileInfo.FILE_TYPE_IMAGE:
                    parent = new File(Environment.getExternalStorageDirectory(), Const.SAVE_IMAGE_PATH);
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    file = new File(parent, FileUtils.removeFileSeparator(fileInfo.getName() + "." + fileInfo.getSuffix()));
                    return file;
                case FileInfo.FILE_TYPE_MUSIC:
                    parent = new File(Environment.getExternalStorageDirectory(), Const.SAVE_MUSIC_PATH);
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    file = new File(parent, FileUtils.removeFileSeparator(fileInfo.getName() + "." + fileInfo.getSuffix()));
                    return file;
                case FileInfo.FILE_TYPE_VIDEO:
                    parent = new File(Environment.getExternalStorageDirectory(), Const.SAVE_VIDEO_PATH);
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    file = new File(parent, FileUtils.removeFileSeparator(fileInfo.getName() + "." + fileInfo.getSuffix()));
                    return file;
                default:
                    break;
            }

        } else {
            return null;
        }
        // 更改文件的上次修改时间为当前创建时间
        file.setLastModified(System.currentTimeMillis());
        return file;

    }


    /**
     * 转换为流量数组
     * String[0] 为数值
     * String[1] 为单位
     * 1024 ===》》》 1 k
     *
     * @param size
     * @return
     */
    public static String[] getFileSizeArrayStr(long size) {
        DecimalFormat decimalFormat = new DecimalFormat("####.#");

        String[] result = new String[2];
        if (size < 0) { //小于0字节则返回0
            result[0] = "0";
            result[1] = "B";
            return result;
        }

        double value = 0f;
        if ((size / 1024) < 1) {
            result[0] = decimalFormat.format(size);
            result[1] = "B";
        } else if ((size / (1024 * 1024)) < 1) {
            value = size / 1024f;
            result[0] = decimalFormat.format(value);
            result[1] = "KB";
        } else if (size / (1024 * 1024 * 1024) < 1) {
            value = (size * 100 / (1024 * 1024)) / 100f;
            result[0] = decimalFormat.format(value);
            result[1] = "MB";
        } else {
            value = (size * 100 / (1024 * 1024 * 1024)) / 100f;
            result[0] = decimalFormat.format(value);
            result[1] = "GB";
        }

        return result;
    }


    /**
     * 去除文件名中包含的文件分隔符。使用空格代替
     *
     * @param fileName
     * @return
     */
    private static String removeFileSeparator(String fileName) {

        String newFileName = fileName;

        if (fileName.contains(File.separator)) {
            newFileName = fileName.replaceAll(File.separator, " ");
            Log.i(TAG, "去除分隔符后的文件名->" + newFileName);

        }

        return newFileName;
    }


    /**
     * 添加一个文件到MediaStore媒体数据库中
     *
     * @param file 要添加的文件
     */
    public static void addFileToMediaStore(Context context, File file) {

        Uri contentUri = Uri.fromFile(file);
        // 发送广播，通知系统将此文件添加到数据库中
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        if (context == null) {
            return;
        }
        context.sendBroadcast(intent);

    }


    public static void main(String[] args) {


    }


}
