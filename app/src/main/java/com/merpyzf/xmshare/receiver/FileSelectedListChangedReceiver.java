package com.merpyzf.xmshare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 接收文件列表发生改变的广播
 */
public abstract class FileSelectedListChangedReceiver extends BroadcastReceiver {


    public static final String ACTION = "ACTION_FILE_LIST_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION)) {
            // 未保证文件不重复的问题，使用文件的全路径来表示
            String unSelectedFile = intent.getStringExtra("unSelectedFile");
            Log.i("w2k", "接收到文件发生改变的广播 -->" + action);
            onFileListChanged(unSelectedFile);
        }
    }
    /**
     * 文件列表发生改变时的回调
     */
    public abstract void onFileListChanged(String unSelectedFile);


}
