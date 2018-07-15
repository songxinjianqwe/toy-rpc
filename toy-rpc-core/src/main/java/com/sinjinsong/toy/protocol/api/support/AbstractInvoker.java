package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public abstract class AbstractInvoker<T> implements Invoker<T> {
    private List<Filter> filters;
    protected ReferenceConfig<T> referenceConfig;
    protected Class<T> interfaceClass;
    
    @Override
    public Class<T> getInterface() {
        return interfaceClass;
    }
    
    @Override
    public final RPCResponse invoke(RPCRequest rpcRequest) throws RPCException {
        filters.forEach(filter -> filter.invoke(this,rpcRequest));
        return doInvoke(rpcRequest);
    }
    
    protected abstract RPCResponse doInvoke(RPCRequest rpcRequest) throws RPCException;


    public void setReferenceConfig(ReferenceConfig<T> referenceConfig) {
        this.referenceConfig = referenceConfig;
    }
    
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }
    
    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
}
