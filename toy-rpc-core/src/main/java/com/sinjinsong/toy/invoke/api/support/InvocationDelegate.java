package com.sinjinsong.toy.invoke.api.support;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.invoke.api.Invocation;
import com.sinjinsong.toy.invoke.api.InvokeProducer;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/17
 */
public class InvocationDelegate implements Invocation {
    private Invocation delegate;
    private InvokeProducer invokeProducer;
    
    public InvocationDelegate(Invocation delegate, InvokeProducer producer) {
        this.delegate = delegate;
        this.invokeProducer = producer;
    }

    public Invocation getDelegate() {
        return delegate;
    }

    @Override
    public RPCResponse invoke() throws RPCException {
        return invokeProducer.invoke();
    }

    @Override
    public String getInterfaceName() {
        return delegate.getInterfaceName();
    }

    @Override
    public String getMethodName() {
        return delegate.getMethodName();
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return delegate.getParameterTypes();
    }

    @Override
    public Object[] getParameters() {
        return delegate.getParameters();
    }

    @Override
    public String getRequestId() {
        return delegate.getRequestId();
    }
}
