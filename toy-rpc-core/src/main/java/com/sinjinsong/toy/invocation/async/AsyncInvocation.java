package com.sinjinsong.toy.invocation.async;

import com.sinjinsong.toy.common.context.RPCThreadLocalContext;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.invocation.api.support.AbstractInvocation;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

import java.util.concurrent.Future;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public class AsyncInvocation extends AbstractInvocation {
    
    @Override
    public Object invoke(RPCRequest request) throws RPCException {
        Future<RPCResponse> future = execute(request);
        RPCThreadLocalContext.getContext().setFuture(future);
        return null;
    }
}
