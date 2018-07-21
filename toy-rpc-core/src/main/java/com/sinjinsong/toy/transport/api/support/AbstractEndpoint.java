package com.sinjinsong.toy.transport.api.support;

import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.Endpoint;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public abstract class AbstractEndpoint implements Endpoint {
    private ServiceURL serviceURL;
    private ApplicationConfig applicationConfig;

    public void init(ApplicationConfig applicationConfig, ServiceURL serviceURL) {
        this.serviceURL = serviceURL;
        this.applicationConfig = applicationConfig;
    }
    
    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public ServiceURL getServiceURL() {
        return serviceURL;
    }
}
