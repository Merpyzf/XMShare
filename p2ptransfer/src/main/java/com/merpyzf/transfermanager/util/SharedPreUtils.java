
package com.merpyzf.transfermanager.util;

import android.content.Context;

import com.merpyzf.transfermanager.common.Const;


/**
 * Created by wangke on 2018/1/11.
 */

public class SharedPreUtils {


    public static void putString(Context context, String spName, String key, String value) {
        context.getSharedPreferences(spName, Context.MODE_PRIVATE)
                .edit()
                .putString(key, value)
                .commit();
    }


    public static String getString(Context context, String spName, String key, String defaultValue) {

        return context.getSharedPreferences(spName, Context.MODE_PRIVATE)
                .getString(key, defaultValue);


    }


    public static void putInteger(Context context, String spName, String key, int value) {
        context.getSharedPreferences(spName, Context.MODE_PRIVATE)
                .edit()
                .putInt(key, value)
                .commit();
    }


    public static int getInteger(Context context, String spName, String key, int defaultValue) {

        return context.getSharedPreferences(spName, Context.MODE_PRIVATE)
                .getInt(key, defaultValue);


    }


}
