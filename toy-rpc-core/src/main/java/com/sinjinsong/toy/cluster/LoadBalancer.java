package com.sinjinsong.toy.core.cluster;

import com.sinjinsong.toy.core.transport.client.endpoint.Endpoint;
import com.sinjinsong.toy.core.transport.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
public interface LoadBalancer {
    Endpoint select(RPCRequest request);
    void close();
}
