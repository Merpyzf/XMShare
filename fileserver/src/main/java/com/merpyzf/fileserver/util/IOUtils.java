package com.merpyzf.fileserver.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * description:
 * author: wangke
 * date: 2018/8/11.
 * version:
 */
public class IOUtils {

    /**
     * 输入流转换成字符串
     *
     * @param is 输入流
     * @return String字符串
     */
    public static String stream2Str(InputStream is) {
        StringBuffer sb = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
