package com.sinjinsong.toy.transport.api.support;

import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.transport.api.Endpoint;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public abstract class AbstractEndpoint implements Endpoint {
    private String address;
    private ApplicationConfig applicationConfig;

    public void init(ApplicationConfig applicationConfig, String address) {
        this.address = address;
        this.applicationConfig = applicationConfig;
    }
    
    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    @Override
    public String getAddress() {
        return address;
    }
}
