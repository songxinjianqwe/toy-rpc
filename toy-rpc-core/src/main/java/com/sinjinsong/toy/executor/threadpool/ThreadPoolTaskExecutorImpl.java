package com.sinjinsong.toy.executor.threadpool;

import com.sinjinsong.toy.executor.api.support.AbstractTaskExecutor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public class ThreadPoolTaskExecutorImpl extends AbstractTaskExecutor {
    private ExecutorService executorService;
    
    @Override
    public void init(Integer threads) {
        executorService = new ThreadPoolExecutor(
                threads,
                threads,
                0,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(),
                new ThreadFactory() {
                    private AtomicInteger atomicInteger = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r,"pool-" + atomicInteger.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );    
    }

    @Override
    public void submit(Runnable runnable) {
        executorService.submit(runnable);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
