package com.merpyzf.xmshare.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by merpyzf on 2018/3/8.
 * 用于处理只需要执行一次的业务逻辑的工具类
 */

public class Once {

    SharedPreferences mSharedPreferences;
    Context mContext;


    public Once(Context context){

        mSharedPreferences = context.getSharedPreferences("once", Context.MODE_PRIVATE);
        mContext = context;

    }

    /**
     * 需要只执行一次的业务逻辑放到这个方法中
     * @param tagKey
     * @param callback
     */
    public void execute(String tagKey,OnceCallback callback){

        boolean isFirstTime = mSharedPreferences.getBoolean(tagKey, true);
        if(isFirstTime){
            callback.onExecute();

            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putBoolean(tagKey, true);
            edit.apply();

        }


    }

    public void execute(int tagKeyResId, OnceCallback callback){
        execute(mContext.getString(tagKeyResId), callback);
    }


    public interface OnceCallback{
        void onExecute();
    }

}
