package com.merpyzf.fileserver.util;

import java.io.File;

/**
 * Created by merpyzf on 2018/4/16.
 * 文件操作相关的工具类
 */

public class FileUtils {
    /**
     * 根据文件路径获取文件后缀名
     *
     * @param filePath
     * @return 当获取不到时返回 ""
     */
    public static String getFileSuffix(String filePath) {
        int beginIndex = filePath.lastIndexOf(".") + 1;
        if (beginIndex == 0) {
            return "";
        }
        return filePath.substring(beginIndex);
    }

    /**
     * 根据File对象获取文件后缀名
     *
     * @param
     * @return 当获取不到时返回 ""
     */
    public static String getFileSuffix(File file) {
        String filePath = file.getPath();
        int beginIndex = filePath.lastIndexOf(".") + 1;
        if (beginIndex == 0) {
            return "";
        }
        return filePath.substring(beginIndex);
    }

}
