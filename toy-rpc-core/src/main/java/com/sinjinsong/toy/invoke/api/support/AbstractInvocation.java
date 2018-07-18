package com.sinjinsong.toy.invoke.api.support;


import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.invoke.api.Invocation;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
public abstract class AbstractInvocation implements Invocation {
    protected ReferenceConfig referenceConfig;
    protected RPCRequest rpcRequest;
    
    public void setReferenceConfig(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    public void setRpcRequest(RPCRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }

    protected abstract Future<RPCResponse> doInvoke();
     
}
