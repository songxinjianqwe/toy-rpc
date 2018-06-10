package com.sinjinsong.rpc.core.loadbalance.impl;

import com.sinjinsong.rpc.core.client.endpoint.Endpoint;
import com.sinjinsong.rpc.core.domain.RPCRequest;
import com.sinjinsong.rpc.core.loadbalance.AbstractLoadBalancer;
import com.sinjinsong.rpc.core.zk.ServiceDiscovery;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */

public class RandomLoadBalancer extends AbstractLoadBalancer {

    public RandomLoadBalancer(ServiceDiscovery serviceDiscovery) {
        super(serviceDiscovery);
    }


    @Override
    protected Endpoint doSelect(List<Endpoint> endpoints, RPCRequest request) {
        if (endpoints.size() == 0) {
            return null;
        }
        return endpoints.get(ThreadLocalRandom.current().nextInt(endpoints.size()));

    }
}
