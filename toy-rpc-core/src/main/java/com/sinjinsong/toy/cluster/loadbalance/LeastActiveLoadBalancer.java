package com.sinjinsong.toy.cluster.loadbalance;

import com.sinjinsong.toy.cluster.support.AbstractLoadBalancer;
import com.sinjinsong.toy.transport.client.Endpoint;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/3/15
 */
public class LeastActiveLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected Endpoint doSelect(List<Endpoint> endpoints, RPCRequest request) {
        return null;
    }
    
}
