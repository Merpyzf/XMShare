package com.merpyzf.transfermanager.util;

import java.text.DecimalFormat;

/**
 * Created by wangke on 2018/1/16.
 */

public class FormatUtils {

    /**
     * 将字byte转换为MB
     *
     * @param
     * @return
     */
    public static float convert2Mb(long b) {
        float fileSize = b / (1024 * 1024 * 1f);
        DecimalFormat decimalFormat = new DecimalFormat(".0");
        return Float.valueOf(decimalFormat.format(fileSize));
    }


    /**
     * 将毫秒转换为 h:s单位
     *
     * @param ms
     * @return
     */
    public static String convertMS2Str(long ms) {

        int second = (int) (ms / 1000);

        int s = (int) (second % 60);

        int h = (int) (second / 60);

        return h + ":" + s;
    }

    /**
     * 字符串转换unicode
     * @param string
     * @return
     */
    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("T" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    /**
     * unicode 转字符串
     * @param unicode 全为 Unicode 的字符串
     * @return
     */
    public static String unicode2String(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("T");

        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);
            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }


}
