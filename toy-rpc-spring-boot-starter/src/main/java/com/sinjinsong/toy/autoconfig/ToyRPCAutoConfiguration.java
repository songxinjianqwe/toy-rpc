package com.sinjinsong.toy.autoconfig;


import com.sinjinsong.toy.autoconfig.condition.RPCServerCondition;
import com.sinjinsong.toy.cluster.support.AbstractLoadBalancer;
import com.sinjinsong.toy.common.enumeration.LoadBalanceType;
import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.config.ClusterConfig;
import com.sinjinsong.toy.config.ProtocolConfig;
import com.sinjinsong.toy.config.RegistryConfig;
import com.sinjinsong.toy.protocol.toy.ToyProtocol;
import com.sinjinsong.toy.proxy.JDKRPCProxyFactory;
import com.sinjinsong.toy.registry.zookeeper.ZkServiceRegistry;
import com.sinjinsong.toy.serialize.protostuff.ProtostuffSerializer;
import com.sinjinsong.toy.transport.server.RPCServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@EnableConfigurationProperties(RPCProperties.class)
@Configuration
@Slf4j
public class ToyRPCAutoConfiguration {
    @Autowired
    private RPCProperties properties;

    @ConditionalOnProperty(value = "rpc.registry.address")
    @Bean(initMethod = "init",destroyMethod="close")
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = properties.getRegistry();
        //TODO 根据type创建ServiceRegistry
        ZkServiceRegistry serviceRegistry = new ZkServiceRegistry(registryConfig);
        registryConfig.setRegistryInstance(serviceRegistry);
        log.info("{}",registryConfig);
        return registryConfig;
    } 

    @ConditionalOnProperty(value = "rpc.application.name")
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig application = properties.getApplication();
        application.setProxyFactoryInstance(new JDKRPCProxyFactory());
        application.setSerializerInstance(new ProtostuffSerializer());
        log.info("{}",application);
        return application;
    }   
    
    @ConditionalOnProperty(value = "rpc.protocol.type")
    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = properties.getProtocol();
        protocolConfig.setProtocolInstance(new ToyProtocol());
        log.info("{}",protocolConfig);
        return protocolConfig;
    }
    
    @ConditionalOnProperty(value = "rpc.cluster.loadbalance")
    @Bean
    public ClusterConfig clusterconfig(ApplicationConfig applicationConfig,RegistryConfig registryConfig) {
        ClusterConfig clusterConfig = properties.getCluster();
        AbstractLoadBalancer loadBalancer = LoadBalanceType.valueOf(clusterConfig.getLoadbalance().toUpperCase()).getLoadBalancer();
        loadBalancer.setSerializer(applicationConfig.getSerializerInstance());
        loadBalancer.setRegistryConfig(registryConfig);
        clusterConfig.setLoadBalanceInstance(loadBalancer);
        log.info("{}",clusterConfig);
        return clusterConfig;
    }
        
    @Conditional(RPCServerCondition.class)
    @Bean
    public RPCServer rpcServer(ApplicationConfig applicationConfig,RegistryConfig registryConfig,ClusterConfig clusterConfig, ProtocolConfig protocolConfig) {
        return new RPCServer(applicationConfig,clusterConfig,registryConfig, protocolConfig);
    }
    
    @Bean
    public RPCConsumerBeanPostProcessor rpcConsumerBeanPostProcessor() {
        return new RPCConsumerBeanPostProcessor();        
    }
}


