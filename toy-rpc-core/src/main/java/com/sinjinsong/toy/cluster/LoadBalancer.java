package com.sinjinsong.toy.cluster;

import com.sinjinsong.toy.transport.client.endpoint.Endpoint;
import com.sinjinsong.toy.transport.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
public interface LoadBalancer {
    Endpoint select(RPCRequest request);
    void close();
}
