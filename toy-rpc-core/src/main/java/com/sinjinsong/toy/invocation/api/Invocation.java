package com.sinjinsong.toy.invocation.api;

import com.sinjinsong.toy.common.domain.RPCRequest;
import com.sinjinsong.toy.common.domain.RPCResponse;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.protocol.api.InvokeParam;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Invocation {
    RPCResponse invoke(InvokeParam invokeParam, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws RPCException;
}
