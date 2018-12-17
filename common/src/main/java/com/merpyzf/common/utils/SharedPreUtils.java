
package com.merpyzf.common.utils;

import android.content.Context;
import android.util.Log;


/**
 * @author wangke
 * @date 2018/1/11
 */

public class SharedPreUtils {
    private SharedPreUtils() {
        throw new Error("Do not need instantiate!");
    }

    public static void putString(Context context, String spName, String key, String value) {
        context.getSharedPreferences(spName, Context.MODE_PRIVATE)
                .edit()
                .putString(key, value)
                .apply();
    }

    public static String getString(Context context, String spName, String key, String defaultValue) {
        String result = context.getSharedPreferences(spName, Context.MODE_PRIVATE).getString(
                key, defaultValue);
        return result;
    }

    public static void putInteger(Context context, String spName, String key, int value) {
        context.getSharedPreferences(spName, Context.MODE_PRIVATE)
                .edit()
                .putInt(key, value)
                .apply();
    }

    public static int getInteger(Context context, String spName, String key, int defaultValue) {
        return context.getSharedPreferences(spName, Context.MODE_PRIVATE)
                .getInt(key, defaultValue);

    }


    public static void putLong(Context context, String spName, String key, long value) {
        context.getSharedPreferences(spName, Context.MODE_PRIVATE)
                .edit()
                .putLong(key, value)
                .apply();
    }

    public static Long getLong(Context context, String spName, String key, long defaultValue) {
        return context.getSharedPreferences(spName, Context.MODE_PRIVATE)
                .getLong(key, defaultValue);

    }

}
