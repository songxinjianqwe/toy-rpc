package com.sinjinsong.toy.cluster.loadbalance;

import com.sinjinsong.toy.cluster.support.AbstractLoadBalancer;
import com.sinjinsong.toy.registry.ServiceRegistry;
import com.sinjinsong.toy.remoting.transport.client.endpoint.Endpoint;
import com.sinjinsong.toy.remoting.transport.domain.RPCRequest;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/3/15
 */
public class LeastActiveLoadBalancer extends AbstractLoadBalancer {

    
    public LeastActiveLoadBalancer(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Endpoint doSelect(List<Endpoint> endpoints, RPCRequest request) {
        return null;
    }
    
}
