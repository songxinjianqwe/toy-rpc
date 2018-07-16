package com.sinjinsong.toy.invoke.api.support;


import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.invoke.api.Invocation;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
public abstract class AbstractInvocation implements Invocation {
    protected ReferenceConfig referenceConfig;
    protected Invoker invoker;
    protected RPCRequest rpcRequest;

    public void setReferenceConfig(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public void setRpcRequest(RPCRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }

    @Override
    public String getInterfaceName() {
        return rpcRequest.getInterfaceName();
    }

    @Override
    public String getMethodName() {
        return rpcRequest.getMethodName();
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return rpcRequest.getParameterTypes();
    }

    @Override
    public Object[] getParameters() {
        return rpcRequest.getParameters();
    }
    
    public Invoker<?> getInvoker() {
        return invoker;
    }

    @Override
    public String getRequestId() {
        return rpcRequest.getRequestId();
    }
     
}
