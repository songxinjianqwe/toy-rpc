package com.sinjinsong.toy.config;

import com.sinjinsong.toy.cluster.FailureHandler;
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
    private String failure;
    private LoadBalancer loadBalanceInstance;
    private FailureHandler failureHandlerInstance;
}
