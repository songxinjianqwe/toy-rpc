package com.sinjinsong.toy.invocation.oneway;

import com.sinjinsong.toy.invocation.api.support.AbstractInvocation;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public  class OneWayInvocation extends AbstractInvocation {
    @Override
    protected RPCResponse doInvoke() throws Throwable {
        doCustomProcess();
        return null;
    }
}
