package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;

/**
 * @author sinjinsong
 * @date 2018/7/15
 */
public abstract class AbstractExporter<T> implements Exporter<T> {
    protected Invoker<T> invoker;
    protected ServiceConfig<T> serviceConfig;
    
    public void setInvoker(Invoker<T> invoker) {
        this.invoker = invoker;
    }

    public void setServiceConfig(ServiceConfig<T> serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    @Override
    public Invoker<T> getInvoker() {
        return invoker;
    }

    @Override
    public ServiceConfig<T> getServiceConfig() {
        return serviceConfig;
    }
}
