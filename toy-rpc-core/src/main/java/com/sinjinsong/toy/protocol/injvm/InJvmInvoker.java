package com.sinjinsong.toy.protocol.injvm;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.support.AbstractInvoker;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/18
 */
public class InJvmInvoker<T> extends AbstractInvoker<T> {

    @Override
    public RPCResponse invoke(InvokeParam invokeParam) throws RPCException {
        return null;
    }
}
