package com.sinjinsong.toy.cluster;

import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.common.domain.RPCRequest;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
public interface LoadBalancer {
    Invoker select(List<Invoker> invokers, RPCRequest request);

    <T> Invoker<T> referCluster(ReferenceConfig<T> referenceConfig);
}
