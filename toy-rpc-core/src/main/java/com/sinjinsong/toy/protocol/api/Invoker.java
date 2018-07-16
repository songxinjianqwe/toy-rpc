package com.sinjinsong.toy.protocol.api;


import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.transport.client.Endpoint;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Invoker<T> {

    Class<T> getInterface();

    RPCResponse invoke(RPCRequest rpcRequest, ReferenceConfig<T> referenceConfig) throws RPCException;
    
    Endpoint getEndpoint();
    
    void setEndpoint(Endpoint endpoint);
}
