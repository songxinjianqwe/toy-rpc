package com.sinjinsong.toy.filter;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Filter {
    RPCResponse invoke(Invoker<?> invoker, RPCRequest rpcRequest) throws RPCException;
}
