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
    private Integer threads;
    private String type;
    private TaskExecutor executorInstance;
}
