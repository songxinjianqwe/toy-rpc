package com.sinjinsong.toy.invoke.oneway;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.invoke.api.support.AbstractInvocation;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public abstract class OneWayInvocation extends AbstractInvocation {
    
    @Override
    public RPCResponse invoke() throws RPCException {
        doInvoke();
        return null;
    }
}
