package com.sinjinsong.toy.config;

import com.sinjinsong.toy.cluster.FaultToleranceHandler;
import com.sinjinsong.toy.cluster.LoadBalancer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterConfig {
    private String loadbalance;
    private String faulttolerance;
    private LoadBalancer loadBalanceInstance;
    private FaultToleranceHandler faultToleranceHandlerInstance;
}
