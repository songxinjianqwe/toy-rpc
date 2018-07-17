package com.sinjinsong.toy.protocol.http;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.invoke.api.Invocation;
import com.sinjinsong.toy.protocol.api.support.AbstractInvoker;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/18
 */
public class HttpInvoker<T> extends AbstractInvoker<T> {
    @Override
    protected RPCResponse doInvoke(Invocation invocation) throws RPCException {
        return null;
    }
}
