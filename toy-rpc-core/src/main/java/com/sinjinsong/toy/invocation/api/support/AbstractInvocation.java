package com.sinjinsong.toy.invocation.api.support;


import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.invocation.api.Invocation;
import com.sinjinsong.toy.transport.client.Endpoint;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
public abstract class AbstractInvocation implements Invocation {
    protected ReferenceConfig referenceConfig;
 
    @Override
    public void setReferenceConfig(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }
    
    protected Future<RPCResponse> execute(RPCRequest request) {
        Endpoint endpoint = referenceConfig.getClusterConfig().getLoadBalanceInstance().select(request);
        if (endpoint != null) {
            return endpoint.submit(request);
        }
        log.error("未找到可用服务器");
        throw new RPCException("未找到可用服务器");
    }
}
