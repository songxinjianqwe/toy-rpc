package com.sinjinsong.toy.cluster.loadbalance;

import com.sinjinsong.toy.cluster.support.AbstractLoadBalancer;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/3/15
 */
public class LeastActiveLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected Invoker doSelect(List<Invoker> invokers, RPCRequest request) {
        
        return null;
    }
    
}
