package com.sinjinsong.toy.executor.api;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public interface TaskExecutor {
    void init(Integer threads);
    void submit(Runnable runnable);
    void close();
}
