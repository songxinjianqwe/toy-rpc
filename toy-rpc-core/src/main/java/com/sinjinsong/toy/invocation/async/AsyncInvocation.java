package com.sinjinsong.toy.invocation.async;

import com.sinjinsong.toy.common.context.RPCThreadLocalContext;
import com.sinjinsong.toy.invocation.api.support.AbstractInvocation;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;

import java.util.concurrent.Future;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public abstract class AsyncInvocation extends AbstractInvocation {
    @Override
    protected RPCResponse doInvoke() throws Throwable {
        Future<RPCResponse> future = getResponseFuture();
        RPCThreadLocalContext.getContext().setFuture(future);
        return null;
    }

}
