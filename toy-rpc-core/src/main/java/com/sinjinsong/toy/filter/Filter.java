package com.sinjinsong.toy.filter;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.Result;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Filter {
    Result invoke(Invoker<?> invoker, RPCRequest rpcRequest) throws RPCException;
}
