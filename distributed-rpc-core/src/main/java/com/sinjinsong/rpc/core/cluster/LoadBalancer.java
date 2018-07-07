package com.sinjinsong.rpc.core.cluster;

import com.sinjinsong.rpc.core.transport.client.endpoint.Endpoint;
import com.sinjinsong.rpc.core.transport.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
public interface LoadBalancer {
    Endpoint select(RPCRequest request);
    void close();
}
