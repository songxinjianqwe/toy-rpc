package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.client.Endpoint;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public abstract class AbstractInvoker<T> implements Invoker<T> {
    protected ReferenceConfig<T> referenceConfig;
    protected Class<T> interfaceClass;
    protected Endpoint endpoint;

    @Override
    public Class<T> getInterface() {
        return interfaceClass;
    }

    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }


    /**
     * @param rpcRequest
     * @return
     * @throws RPCException
     */
    @Override
    public final RPCResponse invoke(RPCRequest rpcRequest) throws RPCException {
        return doInvoke(rpcRequest);
    }

    protected abstract RPCResponse doInvoke(RPCRequest rpcRequest) throws RPCException;


    public void setReferenceConfig(ReferenceConfig<T> referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }
}
