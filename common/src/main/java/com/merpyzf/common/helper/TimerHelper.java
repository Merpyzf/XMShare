package com.merpyzf.common.helper;


import android.os.Handler;


/**
 * @author 郭攀峰
 * @date 2015/9/19
 * 一个通用的定时器
 */
public class TimerHelper implements Runnable {
    private Handler mHandler;
    private int mInterval;
    private Timeout mTimeout;

    private boolean isCycle = true;
    private boolean isCancel = false;

    public TimerHelper(Handler handler, Timeout timeout, int interval, boolean isCycle) {
        mTimeout = timeout;
        mInterval = interval;
        this.isCycle = isCycle;
        if (handler != null) {
            mHandler = handler;
        } else {
            mHandler = new Handler();
        }
    }

    public void start() {
        if (mHandler != null) {
            mHandler.postDelayed(this, mInterval);
        }
    }

    public void cancel() {
        isCancel = true;
    }

    @Override
    public void run() {
        if (!isCancel) {
            if (mTimeout != null) {
                mTimeout.onTimeOut();
            }
            if (isCycle) {
                start();
            }
        }
    }
}
