package com.battcn.bio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Levin
 * @create 2017/8/29 0029
 */
public class TimeServerHandlerExecutePool {
    private ExecutorService service;

    /**
     * 线程池
     *
     * @param maxPoolSize 最大线程数
     * @param queueSize   队列大小
     */
    public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
        service = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize, 120L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueSize));
    }
    public void execute(Runnable task) {
        service.execute(task);
    }

}
