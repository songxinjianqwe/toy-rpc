package com.sinjinsong.toy.executor.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @author sinjinsong
 * @date 2018/7/21
 */
public class TaskEventFactory implements EventFactory<TaskEvent> {
    @Override
    public TaskEvent newInstance() {
        return new TaskEvent();
    }
}
