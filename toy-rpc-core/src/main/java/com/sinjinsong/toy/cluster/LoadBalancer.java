package com.sinjinsong.toy.cluster;

import com.sinjinsong.toy.transport.client.Endpoint;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
public interface LoadBalancer {
    Endpoint select(RPCRequest request);
    void close();
}
