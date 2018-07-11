package com.sinjinsong.toy.cluster.loadbalance;

import com.sinjinsong.toy.cluster.support.AbstractLoadBalancer;
import com.sinjinsong.toy.registry.ServiceRegistry;
import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.transport.client.endpoint.Endpoint;
import com.sinjinsong.toy.transport.domain.RPCRequest;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/3/15
 */
public class LeastActiveLoadBalancer extends AbstractLoadBalancer {


    public LeastActiveLoadBalancer(ServiceRegistry ServiceRegistry, Serializer serializer) {
        super(ServiceRegistry, serializer);
    }

    @Override
    protected Endpoint doSelect(List<Endpoint> endpoints, RPCRequest request) {
        return null;
    }
    
}
