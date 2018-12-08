package com.merpyzf.xmshare.util;

import android.content.Context;

/**
 * Description: 读取和写入用户设置的辅助类
 * Date: 2018/12/8
 *
 * @author wangke
 */
public class PersonalSettingHelper {

    /**
     * sp文件名, 存储用户信息和用户设置的信息
     */
    public static final String SP_USER = "sp_user_info";

    private PersonalSettingHelper() {

    }

    public static String getNickname(Context context) {
        return SharedPreUtils.getString(context, SP_USER, "nickName", "");
    }

}