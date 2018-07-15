package com.sinjinsong.toy.invocation.oneway;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.invocation.api.support.AbstractInvocation;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public class OneWayInvocation extends AbstractInvocation {
    
    @Override
    public RPCResponse invoke( RPCRequest request) throws RPCException {
        invoker.getEndpoint().submit(request);
        return null;
    }
}
