package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.Endpoint;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
@Slf4j
public abstract class AbstractRemoteInvoker<T> extends AbstractInvoker<T> {
    private Endpoint endpoint;

    protected abstract Endpoint doInitEndpoint(ServiceURL serviceURL, ApplicationConfig applicationConfig);

    public final Invoker<T> initEndpoint(ServiceURL serviceURL, ApplicationConfig applicationConfig) {
        Endpoint endpoint = doInitEndpoint(serviceURL, applicationConfig);
        this.endpoint = endpoint;
        return buildFilterChain(ReferenceConfig.getReferenceConfigByInterface(getInterface()).getFilters());
    }

    public final void updateConfig(ServiceURL serviceURL) {
        //TODO 
        this.endpoint.setConfig(serviceURL);
    }

    
    
    
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public ServiceURL getServiceURL() {
        return endpoint.getServiceURL();
    }

    @Override
    public void close() {
        // 如果是重写了getEndpoint方法而非
        getEndpoint().close();
    }
}
