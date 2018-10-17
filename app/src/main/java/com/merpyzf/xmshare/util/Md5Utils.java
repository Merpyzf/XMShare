package com.merpyzf.xmshare.util;


import android.database.Cursor;
import android.util.Log;

import com.merpyzf.transfermanager.entity.FileInfo;
import com.merpyzf.xmshare.bean.model.FileMd5Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 春水碧于天 on 2018/3/7.
 */

public class Md5Utils {

    private static final String TAG = Md5Utils.class.getSimpleName();

    /**
     * 获取文件的Md5
     *
     * @param file
     * @return
     */
    public static String getMd5(File file) {
        InputStream inputStream = null;
        byte[] buffer = new byte[2048];

        int numRead = -1;
        MessageDigest md5;

        try {
            inputStream = new FileInputStream(file);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = inputStream.read(buffer)) != -1) {

                md5.update(buffer, 0, numRead);

            }
            inputStream.close();
            inputStream = null;
            return md5ToString(md5.digest());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } finally {

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

    }

    public final static String getMd5(String s) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }


    private static String md5ToString(byte[] md5Bytes) {
        StringBuilder hexValue = new StringBuilder();
        for (byte b : md5Bytes) {
            int val = ((int) b) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


    /**
     * 生成文件的Md5值并存储到数据库中
     */
    public static void asyncGenerateFileMd5(List<FileInfo> fileList) {


        List<FileInfo> copyFileInfoList = new ArrayList<>();

        copyFileInfoList.addAll(fileList);


        Observable.fromIterable(copyFileInfoList)
                .filter(fileInfo -> {
                    int count = FileMd5Model.where("filename = ?", fileInfo.getPath()).count(FileMd5Model.class);
                    if (count == 1) {
                        return false;
                    }
                    return true;
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(fileInfo -> {
                    String md5 = Md5Utils.getMd5(new File(fileInfo.getPath()));
                    FileMd5Model fileMd5Model = new FileMd5Model();
                    fileMd5Model.setFileName(fileInfo.getPath());
                    fileMd5Model.setMd5(md5);
                    fileMd5Model.save();
                });


    }

    /**
     * 从数据库中获取根据文件计算出的md5值
     *
     * @param fileInfo
     * @return
     */
    public static String getFileMd5(FileInfo fileInfo) {

        Cursor cursor = FileMd5Model.findBySQL("select *from filemd5model where filename = ?", fileInfo.getPath());
        Log.i(TAG, "cursor的长度-->" + cursor.getCount());
        if (cursor.getCount() == 1) {
            cursor.moveToNext();
            String md5 = cursor.getString(cursor.getColumnIndex("md5"));
            return md5;
        } else {
            return "";
        }


    }
}
