package com.sinjinsong.toy.autoconfig;


import com.sinjinsong.toy.autoconfig.condition.RPCClientCondition;
import com.sinjinsong.toy.autoconfig.condition.RPCServerCondition;
import com.sinjinsong.toy.cluster.LoadBalancer;
import com.sinjinsong.toy.cluster.loadbalance.ConsistentHashLoadBalancer;
import com.sinjinsong.toy.cluster.loadbalance.LeastActiveLoadBalancer;
import com.sinjinsong.toy.cluster.loadbalance.RandomLoadBalancer;
import com.sinjinsong.toy.cluster.loadbalance.RoundRobinLoadBalancer;
import com.sinjinsong.toy.proxy.RPCProxyFactoryBeanRegistry;
import com.sinjinsong.toy.registry.ServiceRegistry;
import com.sinjinsong.toy.remoting.transport.client.RPCClient;
import com.sinjinsong.toy.remoting.transport.server.RPCServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
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
    @Autowired
    private ApplicationContext applicationContext;
    private static RPCClient CLIENT;
        
    @Conditional(RPCServerCondition.class)
    @Bean
    public RPCServer rpcServer() {
        log.info("开始初始化RPCServer");
        log.info("配置文件读取结果:{}",properties);
        return new RPCServer(applicationContext.getBean(ServiceRegistry.class));
    }
    
    @ConditionalOnProperty("rpc.registryAddress")
    @Bean
    public ServiceRegistry serviceRegistry() {
        return  new ServiceRegistry(properties.getRegistryAddress());
    }
    
    @ConditionalOnProperty(value = "rpc.loadBalanceStrategy", havingValue = "CONSISTENT_HASH")
    @Bean(name = "CONSISTENT_HASH")
    public ConsistentHashLoadBalancer consistentHashLoadBalancer() {
        return new ConsistentHashLoadBalancer(applicationContext.getBean(ServiceRegistry.class));
    }

    @ConditionalOnProperty(value = "rpc.loadBalanceStrategy",havingValue = "RANDOM")
    @Bean(name = "RANDOM")
    public RandomLoadBalancer randomLoadBalancer() {
        return new RandomLoadBalancer(applicationContext.getBean(ServiceRegistry.class));
    }

    @ConditionalOnProperty(value = "rpc.loadBalanceStrategy", havingValue = "ROUND_ROBIN")
    @Bean(name = "ROUND_ROBIN")
    public RoundRobinLoadBalancer roundRobinLoadBalancer() {
        return new RoundRobinLoadBalancer(applicationContext.getBean(ServiceRegistry.class));
    }

    @ConditionalOnProperty(value = "rpc.loadBalanceStrategy", havingValue = "LEAST_ACTIVE")
    @Bean(name = "LEAST_ACTIVE")
    public LeastActiveLoadBalancer leastActiveLoadBalancer() {
        return new LeastActiveLoadBalancer(applicationContext.getBean(ServiceRegistry.class));
    }
    
    @Conditional(RPCClientCondition.class)
    @Bean   
    public RPCClient rpcClient() {
        log.info("properties:{}", properties);
        LoadBalancer loadBalancer = applicationContext.getBean(properties.getLoadBalanceStrategy().toUpperCase(), LoadBalancer.class);
        CLIENT.setLoadBalancer(loadBalancer);
        return CLIENT;
    }

    /**
     * 因为RPCProxyFactoryBeanRegistry初始化是在常规bean还没有初始化之前进行的，所以是拿不到@Autowired的属性的
     * 只能去直接读配置文件才能得到basePackage
     * @return
     */
    @Bean
    public static RPCProxyFactoryBeanRegistry rpcConsumerProxyFactoryBeanRegistry() {
        log.info("rpcConsumerProxyFactoryBeanRegistry...");
        CLIENT = new RPCClient();
        return new RPCProxyFactoryBeanRegistry(CLIENT);
    }
}


