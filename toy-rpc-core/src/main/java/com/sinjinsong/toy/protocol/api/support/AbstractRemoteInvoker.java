package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.Client;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
@Slf4j
public abstract class AbstractRemoteInvoker<T> extends AbstractInvoker<T> {
    private Client client;
    
    @Override
    public ServiceURL getServiceURL() {
        return getClient().getServiceURL();
    }
    
    /**
     * 拿到一个invoker
     * @return
     */
    protected Client getClient() {
        return client;
    }
    
    @Override
    public boolean isAvailable() {
        return getClient().isAvailable();
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
