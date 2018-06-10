package com.sinjinsong.rpc.core.loadbalance;

import com.sinjinsong.rpc.core.client.endpoint.Endpoint;
import com.sinjinsong.rpc.core.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
public interface LoadBalancer {
    Endpoint select(RPCRequest request);
    void close();
}
