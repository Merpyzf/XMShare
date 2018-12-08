package com.merpyzf.transfermanager.util;

import android.content.Context;

import com.merpyzf.transfermanager.common.Const;

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

    /**
     * 获取用户设置的昵称
     *
     * @param context
     * @return
     */
    public static String getNickname(Context context) {
        return SharedPreUtils.getString(context, SP_USER, "nickName", "");
    }

    /**
     * 获取用户对应的头像
     *
     * @param context
     * @return
     */
    public static int getAvatar(Context context) {
        return SharedPreUtils.getInteger(context, SP_USER, "avatar", 0);
    }

}