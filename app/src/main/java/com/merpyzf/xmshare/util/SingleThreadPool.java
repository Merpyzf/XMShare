package com.merpyzf.xmshare.util;

import android.support.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description:
 * author: wangke
 * date: 2018/8/7.
 * version:
 */
public class SingleThreadPool {

    private static ThreadPoolExecutor sExecutor = null;
    private static int processorNum = Runtime.getRuntime().availableProcessors();

    private SingleThreadPool() {

    }

    public static ThreadPoolExecutor getSingleton() {
        if (sExecutor == null) {
            synchronized (SingleThreadPool.class) {
                if (sExecutor == null) {
                    sExecutor = new ThreadPoolExecutor(processorNum + 1, 2 * processorNum + 1, 100, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
                }
            }
        }
        return sExecutor;
    }
}
