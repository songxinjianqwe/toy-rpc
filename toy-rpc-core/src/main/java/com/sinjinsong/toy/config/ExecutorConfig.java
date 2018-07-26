package com.sinjinsong.toy.config;

import com.sinjinsong.toy.executor.api.TaskExecutor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sinjinsong
 * @date 2018/7/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorConfig {
    public static final Integer DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();
    private Integer threads;
    private String type;
    private TaskExecutor executorInstance;
    
    public int getThreads() {
        if (threads != null) {
            return threads;
        }
        return DEFAULT_THREADS;
    }
    
    
    public void close() {
        executorInstance.close();
    }
}
