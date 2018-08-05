package com.sinjinsong.toy.autoconfig.beanpostprocessor;

import com.sinjinsong.toy.config.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author sinjinsong
 * @date 2018/7/15
 */
public abstract class AbstractRPCBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    private GlobalConfig globalConfig;
    protected ApplicationContext ctx;

    public void init(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, ProtocolConfig protocolConfig, RegistryConfig registryConfig) {
        globalConfig = GlobalConfig.builder()
                .applicationConfig(applicationConfig)
                .protocolConfig(protocolConfig)
                .registryConfig(registryConfig)
                .clusterConfig(clusterConfig)
                .build();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    protected void initConfig(AbstractConfig config) {
        config.init(
                globalConfig
        );
    }

    public static void initConfig(ApplicationContext ctx, AbstractConfig config) {
        config.init(
                GlobalConfig.builder()
                        .applicationConfig(ctx.getBean(ApplicationConfig.class))
                        .protocolConfig(ctx.getBean(ProtocolConfig.class))
                        .registryConfig(ctx.getBean(RegistryConfig.class))
                        .clusterConfig(ctx.getBean(ClusterConfig.class))
                        .build()
        );
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}

