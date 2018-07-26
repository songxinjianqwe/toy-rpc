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

    //TODO 判断一下需不需要在这里就建立连接
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
    
    @Override
    public void updateServiceConfig(ServiceURL serviceURL) {
        this.serviceURL = serviceURL;
    }
}
