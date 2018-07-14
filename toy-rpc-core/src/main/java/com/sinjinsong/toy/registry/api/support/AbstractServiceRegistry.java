package com.sinjinsong.toy.registry.api.support;

import com.sinjinsong.toy.config.RegistryConfig;
import com.sinjinsong.toy.registry.api.ServiceRegistry;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry {
    protected RegistryConfig registryConfig;

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }
    
    
}
