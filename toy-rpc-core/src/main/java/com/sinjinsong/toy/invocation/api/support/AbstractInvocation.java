package com.sinjinsong.toy.invocation.api.support;


import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.invocation.api.Invocation;
import com.sinjinsong.toy.common.domain.RPCRequest;
import com.sinjinsong.toy.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
public abstract class AbstractInvocation implements Invocation {
    private ReferenceConfig referenceConfig;
    private RPCRequest rpcRequest;
    private Function<RPCRequest, Future<RPCResponse>> processor;
    
    
    public final void setReferenceConfig(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    public final void setRpcRequest(RPCRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }
    
    public final void setProcessor(Function<RPCRequest, Future<RPCResponse>> processor){
        this.processor = processor;
    }
    
    protected final Future<RPCResponse> doCustomProcess() {
        return processor.apply(rpcRequest);
    }
        
    @Override
    public final RPCResponse invoke() throws RPCException {
        RPCResponse response;
        try {
            response = doInvoke();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RPCException(e,ErrorEnum.TRANSPORT_FAILURE, "transport异常");
        }
        return response;
    }

    /**
     * 执行对应子类的调用逻辑，可以抛出任何异常
     *
     * @return
     * @throws Throwable
     */
    protected abstract RPCResponse doInvoke() throws Throwable;

    public final ReferenceConfig getReferenceConfig() {
        return referenceConfig;
    }
    
    public final RPCRequest getRpcRequest() {
        return rpcRequest;
    }
}
