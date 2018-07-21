package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.api.Endpoint;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
@Slf4j
public abstract class AbstractRemoteInvoker<T> extends AbstractInvoker<T> {
    private Endpoint endpoint;
    
    public final Invoker<T> initEndpoint(String address, ApplicationConfig applicationConfig) {
        Endpoint endpoint = doInitEndpoint(address, applicationConfig);
        this.endpoint = endpoint;
        return buildFilterChain(ReferenceConfig.getReferenceConfigByInterface(getInterface()).getFilters());
    }
    
    protected abstract Endpoint doInitEndpoint(String address, ApplicationConfig applicationConfig);


    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getAddress() {
        return getEndpoint().getAddress();
    }

    @Override
    public void close() {
        // 如果是重写了getEndpoint方法而非
        getEndpoint().close();
    }
}
