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
    /**
     * 初始endpoint
     * @param serviceURL
     * @param applicationConfig
     */
    protected abstract void doInitEndpoint(ServiceURL serviceURL, ApplicationConfig applicationConfig);
    
    public final Invoker<T> initEndpoint(ServiceURL serviceURL, ApplicationConfig applicationConfig) {
        doInitEndpoint(serviceURL, applicationConfig);
        return buildFilterChain(ReferenceConfig.getReferenceConfigByInterfaceName(getInterfaceName()).getFilters());
    }

    /**
     * 把endpoint放到每个具体实现中去，是为了避免Endpoint接口暴露出该方法
     * @param serviceURL
     */
    public abstract void updateServiceConfig(ServiceURL serviceURL);
        
    @Override
    public ServiceURL getServiceURL() {
        return getEndpoint().getServiceURL();
    }

    protected abstract Endpoint getEndpoint();
    
    @Override
    public void close() {
        // 如果是重写了getEndpoint方法而非
        getEndpoint().close();
    }
}
