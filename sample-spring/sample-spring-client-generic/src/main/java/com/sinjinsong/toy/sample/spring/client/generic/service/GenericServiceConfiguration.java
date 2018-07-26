package com.sinjinsong.toy.sample.spring.client.generic.service;

import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.config.ClusterConfig;
import com.sinjinsong.toy.config.ProtocolConfig;
import com.sinjinsong.toy.config.RegistryConfig;
import com.sinjinsong.toy.config.generic.RPCGenericServiceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sinjinsong
 * @date 2018/7/23
 */
@Configuration
public class GenericServiceConfiguration {
    
    
    @Bean(name="helloService")
    public RPCGenericServiceBean helloService(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, ProtocolConfig protocolConfig, RegistryConfig registryConfig) {
        RPCGenericServiceBean bean = new RPCGenericServiceBean();
        bean.init(
                "com.sinjinsong.toy.sample.spring.api.service.HelloService",
                false,
                false,
                false,
                3000,
                "",
                1,
                applicationConfig,
                clusterConfig,
                protocolConfig,
                registryConfig
        );
        return bean;
    }
}
