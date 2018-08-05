package com.sinjinsong.toy.executor.disruptor;

import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/21
 */
@Slf4j
public class TaskWorkHandler implements WorkHandler<TaskEvent> {
    @Override
    public void onEvent(TaskEvent event) throws Exception {
        log.info("Thread#currentThread{} onEvent",Thread.currentThread());
        event.getTask().run();
    }
}
