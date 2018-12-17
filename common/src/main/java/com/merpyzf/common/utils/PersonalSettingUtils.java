package com.merpyzf.common.utils;

import android.content.Context;
import android.os.Build;

/**
 * Description: 读取和写入用户设置的辅助类
 * Date: 2018/12/8
 *
 * @author wangke
 */
public class PersonalSettingUtils {

    /**
     * sp文件名, 存储用户信息和用户设置的信息
     */
    private static final String SP_USER = "sp_user_info";
    private static final String KEY_NICKNAME = "nickName";
    private static final String KEY_AVATAR = "avatar";
    public static final String KEY_TRANSFER_MODE = "transfer_mode";
    public static final String KEY_IS_SHOW_HIDDEN_FILE = "is_show_hidden_file";
    public static final String KEY_IS_CLOSE_CURR_PAGE = "is_close_curr_page";
    public static final String KEY_SAVED_NET_FLOW = "saved_net_flow";


    /**
     * 使用已连接的局域网传输模式
     */
    public static final int TRANSFER_MODE_LAN = 0x001;
    /**
     * 通过建立热点组件局域网的方式传输模式
     */
    public static final int TRANSFER_MODE_AP = 0x002;
    public static final int SHOW_HIDDEN_FILE = 0x003;
    public static final int DONT_SHOW_HIDDEN_FILE = 0x004;
    public static final int CLOSE_CURRENT_PAGE_WHEN_ERROR = 0x005;
    public static final int DONT_CLOSE_CURRENT_PAGE_WHEN_ERROR = 0x006;


    private PersonalSettingUtils() {
        throw new Error("Do not need instantiate!");
    }


    public static void saveNickname(Context context, String nickname) {
        SharedPreUtils.putString(context, SP_USER, KEY_NICKNAME, nickname);
    }


    public static void saveAvatar(Context context, int avatarPos) {
        SharedPreUtils.putInteger(context, SP_USER, KEY_AVATAR, avatarPos);
    }

    /**
     * 获取用户设置的昵称
     *
     * @param context
     * @return
     */
    public static String getNickname(Context context) {
        return SharedPreUtils.getString(context, SP_USER, KEY_NICKNAME, Build.MODEL);
    }

    /**
     * 获取用户对应的头像
     *
     * @param context
     * @return
     */
    public static int getAvatar(Context context) {
        return SharedPreUtils.getInteger(context, SP_USER, KEY_AVATAR, 0);
    }

    public static int getTransferMode(Context context) {
        return SharedPreUtils.getInteger(context, SP_USER, KEY_TRANSFER_MODE, TRANSFER_MODE_LAN);
    }

    public static void updateTransferMode(Context context, int transferMode) {
        SharedPreUtils.putInteger(context, SP_USER, KEY_TRANSFER_MODE,
                transferMode);
    }

    public static void updateIsShowHiddenFile(Context context, int mode) {
        SharedPreUtils.putInteger(context, SP_USER, KEY_IS_SHOW_HIDDEN_FILE, mode);
    }

    public static boolean getIsShowHiddenFile(Context context) {
        //默认设置为隐藏
        int mode = SharedPreUtils.getInteger(context, SP_USER, KEY_IS_SHOW_HIDDEN_FILE,
                DONT_SHOW_HIDDEN_FILE);
        if (mode == DONT_SHOW_HIDDEN_FILE) {
            return false;
        } else {
            return true;
        }
    }

    public static void updateIsCloseCurrPageWhenError(Context context, int mode) {
        SharedPreUtils.putInteger(context, SP_USER, KEY_IS_CLOSE_CURR_PAGE, mode);
    }

    public static int getIsCloseCurrPageWhenError(Context context) {
        return SharedPreUtils.getInteger(context, SP_USER, KEY_IS_CLOSE_CURR_PAGE, CLOSE_CURRENT_PAGE_WHEN_ERROR);
    }

    public static void updateSavedNetFlow(Context context, long value) {
        value = value + getSavedNetFlow(context);
        SharedPreUtils.putLong(context, SP_USER, KEY_SAVED_NET_FLOW, value);
    }

    public static Long getSavedNetFlow(Context context) {
        return SharedPreUtils.getLong(context, SP_USER, KEY_SAVED_NET_FLOW, 0L);
    }

}