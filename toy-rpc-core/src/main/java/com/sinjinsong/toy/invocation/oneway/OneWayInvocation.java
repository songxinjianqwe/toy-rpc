package com.sinjinsong.toy.invocation.oneway;

import com.sinjinsong.toy.common.domain.RPCRequest;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.invocation.api.support.AbstractInvocation;
import com.sinjinsong.toy.common.domain.RPCResponse;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public  class OneWayInvocation extends AbstractInvocation {

    @Override
    protected RPCResponse doInvoke(RPCRequest rpcRequest, ReferenceConfig referenceConfig, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws Throwable {
        requestProcessor.apply(rpcRequest);
        return null;
    }
}
