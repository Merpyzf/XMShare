package com.merpyzf.common.manager;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: 线程池的封装
 * Date: 2018/12/8
 *
 * @author wangke
 */
public class ThreadPoolManager {

    private static ThreadPoolManager sInstance = null;
    private ThreadPoolExecutor executor;
    /**
     * 核心线程池大小
     */
    private int corePoolSize;
    /**
     * 最大线程池大小
     */
    private int maximumPoolSize;

    /**
     * 线程池中超过corePoolSize数目的空闲线程的最大存活时间
     */
    private long keepAliveTime = 1;

    /**
     * 时间单位
     */
    private TimeUnit unit = TimeUnit.HOURS;

    public static ThreadPoolManager getInstance() {
        if (sInstance == null) {
            synchronized (ThreadPoolManager.class) {
                if (sInstance == null) {
                    sInstance = new ThreadPoolManager();
                }
            }
        }
        return sInstance;
    }


    public ThreadPoolManager() {
        this.corePoolSize = corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        this.maximumPoolSize = corePoolSize;
        createThreadPoolExecutor();


    }

    private void createThreadPoolExecutor() {

        executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                new LinkedBlockingDeque<Runnable>(),
                // 设置线程的拒绝策略
                new ThreadPoolExecutor.AbortPolicy()
        );

    }

    /**
     * 执行任务
     */
    public void execute(Runnable runnable) {
        if (runnable != null) {
            executor.execute(runnable);
        }
    }

    /**
     * 移除任务
     *
     * @param runnable
     */
    public void remove(Runnable runnable) {
        if (runnable != null) {
            executor.remove(runnable);
        }
    }

}
