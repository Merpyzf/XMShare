package com.merpyzf.xmshare.util;

import android.content.Context;

import com.merpyzf.xmshare.common.Const;

public class SettingHelper {

    public static boolean showHiddenFile(Context context) {
        //默认设置为隐藏
        int content = SharedPreUtils.getInteger(context, Const.SP_USER, Const.IS_SHOW_HIDDEN_FILE, Const.DONT_SHOW_HIDDEN_FILE);
        if (content == Const.DONT_SHOW_HIDDEN_FILE) {
            return false;
        } else {
            return true;
        }
    }

}
