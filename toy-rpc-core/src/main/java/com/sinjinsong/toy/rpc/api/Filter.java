package com.sinjinsong.toy.rpc.api;

import com.sinjinsong.toy.common.exception.RPCException;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Filter {
    Result invoke(Invoker<?> invoker, Invocation invocation) throws RPCException;
}
