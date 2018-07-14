package com.sinjinsong.toy.autoconfig;


import com.sinjinsong.toy.autoconfig.condition.RPCClientCondition;
import com.sinjinsong.toy.autoconfig.condition.RPCServerCondition;
import com.sinjinsong.toy.cluster.LoadBalancer;
import com.sinjinsong.toy.cluster.loadbalance.ConsistentHashLoadBalancer;
import com.sinjinsong.toy.cluster.loadbalance.LeastActiveLoadBalancer;
import com.sinjinsong.toy.cluster.loadbalance.RandomLoadBalancer;
import com.sinjinsong.toy.cluster.loadbalance.RoundRobinLoadBalancer;
import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.config.ProtocolConfig;
import com.sinjinsong.toy.proxy.RPCProxyFactoryBeanRegistry;
import com.sinjinsong.toy.registry.ServiceRegistry;
import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.serialize.protostuff.ProtostuffSerializer;
import com.sinjinsong.toy.transport.client.RPCClient;
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
    private static RPCClient CLIENT;

    @Bean
    public Serializer serializer() {
        return new ProtostuffSerializer();
    }
    
    @ConditionalOnProperty(value = "rpc.registry.address")
    @Bean
    public ServiceRegistry serviceRegistry() {
        log.info("{}",properties.getRegistry());
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        serviceRegistry.setRegistryConfig(properties.getRegistry());
        serviceRegistry.init();
        return serviceRegistry;
    } 

    @ConditionalOnProperty(value = "rpc.application.name")
    @Bean
    public ApplicationConfig applicationConfig() {
        log.info("{}",properties.getApplication());
        return properties.getApplication();
    }

    @ConditionalOnProperty(value = "rpc.protocol.type")
    @Bean
    public ProtocolConfig protocolConfig() {
        log.info("{}",properties.getProtocol());
        return properties.getProtocol();
    }
    
    @ConditionalOnProperty(value = "rpc.cluster.loadbalance", havingValue = "CONSISTENT_HASH")
    @Bean
    public ConsistentHashLoadBalancer consistentHashLoadBalancer(ServiceRegistry serviceRegistry,Serializer serializer) {
        log.info("{}",properties.getCluster());
        ConsistentHashLoadBalancer loadBalancer = new ConsistentHashLoadBalancer();
        loadBalancer.setServiceRegistry(serviceRegistry);
        loadBalancer.setSerializer(serializer);
        loadBalancer.setClusterConfig(properties.getCluster());
        return loadBalancer;
    }
    
    @ConditionalOnProperty(value = "rpc.cluster.loadbalance", havingValue = "RANDOM")
    @Bean
    public LoadBalancer randomLoadBalancer(ServiceRegistry serviceRegistry, Serializer serializer) {
         log.info("{}",properties.getCluster());
        RandomLoadBalancer loadBalancer = new RandomLoadBalancer();
        loadBalancer.setServiceRegistry(serviceRegistry);
        loadBalancer.setSerializer(serializer);
        loadBalancer.setClusterConfig(properties.getCluster());
        return loadBalancer;
    }
    
    @ConditionalOnProperty(value = "rpc.cluster.loadbalance", havingValue = "ROUND_ROBIN")
    @Bean
    public LoadBalancer roundRobinLoadBalancer(ServiceRegistry serviceRegistry,Serializer serializer) {
         log.info("{}",properties.getCluster());
        RoundRobinLoadBalancer loadBalancer = new RoundRobinLoadBalancer();
        loadBalancer.setServiceRegistry(serviceRegistry);
        loadBalancer.setSerializer(serializer);
        loadBalancer.setClusterConfig(properties.getCluster());
        return loadBalancer;
    }
    
    @ConditionalOnProperty(value = "rpc.cluster.loadbalance", havingValue = "LEAST_ACTIVE")
    @Bean
    public LoadBalancer leastActiveLoadBalancer(ServiceRegistry serviceRegistry,Serializer serializer) {
         log.info("{}",properties.getCluster());
        LeastActiveLoadBalancer loadBalancer = new LeastActiveLoadBalancer();
        loadBalancer.setServiceRegistry(serviceRegistry);
        loadBalancer.setSerializer(serializer);
        loadBalancer.setClusterConfig(properties.getCluster());
        return loadBalancer;
    }
    
    @Conditional(RPCServerCondition.class)
    @Bean
    public RPCServer rpcServer(ServiceRegistry serviceRegistry,Serializer serializer,ProtocolConfig protocolConfig) {
        return new RPCServer(serviceRegistry, serializer, protocolConfig);
    }
    
    @Conditional(RPCClientCondition.class)
    @Bean
    public RPCClient rpcClient(LoadBalancer loadBalancer) {
        CLIENT.setLoadBalancer(loadBalancer);
        return CLIENT;
    }
    
    /**
     * 因为RPCProxyFactoryBeanRegistry初始化是在常规bean还没有初始化之前进行的，所以是拿不到@Autowired的属性的
     * 只能去直接读配置文件才能得到basePackage
     *
     * @return
     */
    @Bean
    public static RPCProxyFactoryBeanRegistry rpcConsumerProxyFactoryBeanRegistry() {
        CLIENT = new RPCClient();
        return new RPCProxyFactoryBeanRegistry(CLIENT);
    }
}


