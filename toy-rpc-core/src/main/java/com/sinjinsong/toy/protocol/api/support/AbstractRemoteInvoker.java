package com.sinjinsong.toy.protocol.api.support;

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
    
    @Override
    public ServiceURL getServiceURL() {
        return getEndpoint().getServiceURL();
    }
    
    /**
     * 拿到一个invoker
     * @return
     */
    protected  Endpoint getEndpoint() {
        return endpoint;
    }
    
    @Override
    public boolean isAvailable() {
        return getEndpoint().isAvailable();
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }
}
