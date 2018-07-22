package com.sinjinsong.toy.common.enumeration;

import com.sinjinsong.toy.cluster.loadbalance.*;
import com.sinjinsong.toy.cluster.support.AbstractLoadBalancer;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public enum LoadBalanceType {
    LEAST_ACTIVE(new LeastActiveLoadBalancer()),
    RANDOM(new RandomLoadBalancer()),
    CONSITITENT_HASH(new ConsistentHashLoadBalancer()),
    ROUND_ROBIN(new RoundRobinLoadBalancer()),
    WEIGHTED_RANDOM(new WeightedRandomLoadBalancer());
    
    private AbstractLoadBalancer loadBalancer;
    
    LoadBalanceType(AbstractLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
    
    public AbstractLoadBalancer getLoadBalancer() {
        return loadBalancer;
    }
}
