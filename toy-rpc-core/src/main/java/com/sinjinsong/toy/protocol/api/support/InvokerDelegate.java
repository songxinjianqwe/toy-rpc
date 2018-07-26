package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */
public abstract class InvokerDelegate<T> extends AbstractInvoker<T> {
    private Invoker<T> delegate;

    
    public InvokerDelegate(Invoker<T> delegate) {
        this.delegate = delegate;
    }

    public Invoker<T> getDelegate() {
        return delegate;
    }

    @Override
    public int hashCode() {
        return delegate.getInterface().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Invoker) {
            Invoker rhs = (Invoker) obj;
            return delegate.getInterface().equals(rhs.getInterface());
        }
        return false;
    }
    
        
    @Override
    public boolean isAvailable() {
        return delegate.isAvailable();
    }

    @Override
    public Class<T> getInterface() {
        return delegate.getInterface();
    }

    @Override
    public String getInterfaceName() {
        return delegate.getInterfaceName();
    }

    @Override
    public ServiceURL getServiceURL() {
        return delegate.getServiceURL();
    }
    
    @Override
    public abstract RPCResponse invoke(InvokeParam invokeParam) throws RPCException;
}
