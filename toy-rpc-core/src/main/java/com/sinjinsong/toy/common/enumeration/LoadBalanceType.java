package com.sinjinsong.toy.common.enumeration;

import com.sinjinsong.toy.cluster.loadbalance.*;
import com.sinjinsong.toy.cluster.support.AbstractLoadBalancer;
import com.sinjinsong.toy.common.enumeration.support.ExtensionBaseType;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public enum LoadBalanceType implements ExtensionBaseType<AbstractLoadBalancer> {
    LEAST_ACTIVE(new LeastActiveLoadBalancer()),
    RANDOM(new RandomLoadBalancer()),
    CONSITITENT_HASH(new ConsistentHashLoadBalancer()),
    ROUND_ROBIN(new RoundRobinLoadBalancer()),
    WEIGHTED_RANDOM(new WeightedRandomLoadBalancer());
    
    private AbstractLoadBalancer loadBalancer;
    
    LoadBalanceType(AbstractLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public AbstractLoadBalancer getInstance() {
        return loadBalancer;
    }
}
