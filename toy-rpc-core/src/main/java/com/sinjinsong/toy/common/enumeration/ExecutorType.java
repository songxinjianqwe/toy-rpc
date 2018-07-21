package com.sinjinsong.toy.common.enumeration;

import com.sinjinsong.toy.executor.api.TaskExecutor;
import com.sinjinsong.toy.executor.disruptor.DisruptorTaskExecutorImpl;
import com.sinjinsong.toy.executor.threadpool.ThreadPoolTaskExecutorImpl;

/**
 * @author sinjinsong
 * @date 2018/7/21
 */
public enum ExecutorType {
    THREADPOOL(new ThreadPoolTaskExecutorImpl()),DISRUPTOR(new DisruptorTaskExecutorImpl());
    private TaskExecutor executor;

    ExecutorType(TaskExecutor executor) {
        this.executor = executor;
    }

    public TaskExecutor getExecutor() {
        return executor;
    }
}
