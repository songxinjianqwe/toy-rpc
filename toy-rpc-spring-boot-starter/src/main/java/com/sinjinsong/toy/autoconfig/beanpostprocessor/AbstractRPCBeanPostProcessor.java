package com.sinjinsong.toy.autoconfig.beanpostprocessor;

import com.sinjinsong.toy.config.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author sinjinsong
 * @date 2018/7/15
 */
public abstract class AbstractRPCBeanPostProcessor implements BeanPostProcessor {
    private ApplicationConfig applicationConfig;
    private ClusterConfig clusterConfig;
    private ProtocolConfig protocolConfig;
    private RegistryConfig registryConfig;

    public void init(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, ProtocolConfig protocolConfig, RegistryConfig registryConfig) {
        this.applicationConfig = applicationConfig;
        this.clusterConfig = clusterConfig;
        this.protocolConfig = protocolConfig;
        this.registryConfig = registryConfig;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    protected void initConfig(AbstractConfig config) {
        config.init(
                applicationConfig,
                clusterConfig,
                protocolConfig,
                registryConfig
        );
    }

}
