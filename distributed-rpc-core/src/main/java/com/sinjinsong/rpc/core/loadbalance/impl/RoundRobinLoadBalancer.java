package com.sinjinsong.rpc.core.loadbalance.impl;

import com.sinjinsong.rpc.core.client.endpoint.Endpoint;
import com.sinjinsong.rpc.core.domain.RPCRequest;
import com.sinjinsong.rpc.core.loadbalance.AbstractLoadBalancer;
import com.sinjinsong.rpc.core.zk.ServiceDiscovery;

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
