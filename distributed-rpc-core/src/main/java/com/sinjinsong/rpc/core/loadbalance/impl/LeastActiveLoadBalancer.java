package com.sinjinsong.rpc.core.loadbalance.impl;

import com.sinjinsong.rpc.core.client.endpoint.Endpoint;
import com.sinjinsong.rpc.core.domain.RPCRequest;
import com.sinjinsong.rpc.core.loadbalance.AbstractLoadBalancer;
import com.sinjinsong.rpc.core.zk.ServiceDiscovery;

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
