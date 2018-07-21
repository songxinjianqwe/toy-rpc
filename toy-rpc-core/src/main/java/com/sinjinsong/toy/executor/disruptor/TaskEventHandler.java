package com.sinjinsong.toy.executor.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * @author sinjinsong
 * @date 2018/7/21
 */
public class TaskEventHandler implements EventHandler<TaskEvent> {
    @Override
    public void onEvent(TaskEvent taskEvent, long l, boolean b) throws Exception {
        taskEvent.getTask().run();
    }
}
