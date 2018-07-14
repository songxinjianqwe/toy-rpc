package com.sinjinsong.toy.cluster.loadbalance;

import com.sinjinsong.toy.cluster.support.AbstractLoadBalancer;
import com.sinjinsong.toy.transport.client.endpoint.Endpoint;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
public class RandomLoadBalancer extends AbstractLoadBalancer {


    @Override
    protected Endpoint doSelect(List<Endpoint> endpoints, RPCRequest request) {
        if (endpoints.size() == 0) {
            return null;
        }
        return endpoints.get(ThreadLocalRandom.current().nextInt(endpoints.size()));

    }
}
