package com.sinjinsong.toy.cluster;

import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
public interface LoadBalancer {
    Invoker select(RPCRequest request);

    <T> Invoker<T> register(Class<T> interfaceClass);

    void close();
}
