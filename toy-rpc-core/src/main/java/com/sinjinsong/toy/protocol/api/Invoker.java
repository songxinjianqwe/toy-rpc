package com.sinjinsong.toy.protocol.api;


import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.invoke.api.Invocation;
import com.sinjinsong.toy.transport.client.Endpoint;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Invoker<T> {

    Class<T> getInterface();

    RPCResponse invoke(Invocation invocation) throws RPCException;
    
    Endpoint getEndpoint();
   
}
