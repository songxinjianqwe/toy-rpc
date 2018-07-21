package com.sinjinsong.toy.executor.disruptor;

import lombok.Data;

/**
 * @author sinjinsong
 * @date 2018/7/21
 */
@Data
public class TaskEvent {
    private Runnable task;
}
