package com.sinjinsong.rpc.core.cluster.loadbalance.impl;

import com.sinjinsong.rpc.core.transport.client.endpoint.Endpoint;
import com.sinjinsong.rpc.core.transport.domain.RPCRequest;
import com.sinjinsong.rpc.core.cluster.loadbalance.AbstractLoadBalancer;
import com.sinjinsong.rpc.core.registry.ServiceDiscovery;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {
    private int index = 0;

    public RoundRobinLoadBalancer(ServiceDiscovery serviceDiscovery) {
        super(serviceDiscovery);
    }

    @Override
    protected Endpoint doSelect(List<Endpoint> endpoints, RPCRequest request) {
         if(endpoints.size() == 0) {
            return null;
        }
        Endpoint result = endpoints.get(index);
        index = (index + 1) % endpoints.size();
        return result;
    }
}
