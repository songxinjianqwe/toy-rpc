package com.sinjinsong.toy.cluster.loadbalance.impl;

import com.sinjinsong.toy.cluster.loadbalance.AbstractLoadBalancer;
import com.sinjinsong.toy.registry.ServiceDiscovery;
import com.sinjinsong.toy.transport.client.endpoint.Endpoint;
import com.sinjinsong.toy.transport.domain.RPCRequest;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/3/15
 */
public class LeastActiveLoadBalancer extends AbstractLoadBalancer {

    
    public LeastActiveLoadBalancer(ServiceDiscovery serviceDiscovery) {
        super(serviceDiscovery);
    }

    @Override
    protected Endpoint doSelect(List<Endpoint> endpoints, RPCRequest request) {
        return null;
    }
    
}
