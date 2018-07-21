package com.sinjinsong.toy.executor.disruptor;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.sinjinsong.toy.executor.api.support.AbstractTaskExecutor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public class DisruptorTaskExecutorImpl extends AbstractTaskExecutor {
    private Disruptor<TaskEvent> disruptor;
    private TaskEventFactory eventFactory;
    private static final int RING_BUFFER_SIZE = 1024 * 1024; 
    
    @Override
    public void init(Integer threads) {
        // threads在这里不需要，可以是null
        eventFactory = new TaskEventFactory();
        disruptor =
                new Disruptor<>(eventFactory, RING_BUFFER_SIZE, new ThreadFactory() {
                    private AtomicInteger atomicInteger = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "biz-" + atomicInteger.getAndIncrement());
                    }
                }, ProducerType.SINGLE, new YieldingWaitStrategy());
        // 连接消费事件方法
        disruptor.handleEventsWith(new TaskEventHandler());
        // 启动
        disruptor.start();
    }

    @Override
    public void submit(Runnable runnable) {
        //1.可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
        long sequence = disruptor.getRingBuffer().next();
        try {
            //2.用上面的索引取出一个空的事件用于填充（获取该序号对应的事件对象）
            TaskEvent event = disruptor.getRingBuffer().get(sequence);
            //3.获取要通过事件传递的业务数据
            event.setTask(runnable);
        } finally {
            //4.发布事件
            //注意，最后的 ringBuffer.publish 方法必须包含在 finally 中以确保必须得到调用；如果某个请求的 sequence 未被提交，将会堵塞后续的发布操作或者其它的 producer。
            disruptor.getRingBuffer().publish(sequence);
        }
    }

    @Override
    public void close() {
        disruptor.shutdown();
    }
}
