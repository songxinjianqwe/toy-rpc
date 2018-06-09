package com.sinjinsong.rpc.autoconfig.client;


import com.sinjinsong.rpc.core.client.RPCClient;
import com.sinjinsong.rpc.core.loadbalance.LoadBalancer;
import com.sinjinsong.rpc.core.loadbalance.impl.ConsistentHashLoadBalancer;
import com.sinjinsong.rpc.core.loadbalance.impl.LeastActiveLoadBalancer;
import com.sinjinsong.rpc.core.loadbalance.impl.RandomLoadBalancer;
import com.sinjinsong.rpc.core.loadbalance.impl.RoundRobinLoadBalancer;
import com.sinjinsong.rpc.core.client.proxy.RPCConsumerProxyFactoryBeanRegistry;
import com.sinjinsong.rpc.core.util.PropertyUtil;
import com.sinjinsong.rpc.core.zk.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@Configuration
@EnableConfigurationProperties(RPCClientProperties.class)
@ConditionalOnMissingBean(RPCConsumerProxyFactoryBeanRegistry.class)
@Slf4j
public class RPCClientAutoConfiguration {
    @Autowired
    private RPCClientProperties properties;
    @Autowired
    private ApplicationContext applicationContext;
    private static RPCClient CLIENT;


    @Bean(name = "CONSISTENT_HASH")
    public ConsistentHashLoadBalancer consistentHashLoadBalancer() {
        return new ConsistentHashLoadBalancer();
    }

    @Bean(name = "RANDOM")
    public RandomLoadBalancer randomLoadBalancer() {
        return new RandomLoadBalancer();
    }

    @Bean(name = "RR")
    public RoundRobinLoadBalancer roundRobinLoadBalancer() {
        return new RoundRobinLoadBalancer();
    }
    
    @Bean(name = "LEAST_ACTIVE")
    public LeastActiveLoadBalancer leastActiveLoadBalancer() {
        return new LeastActiveLoadBalancer();
    }
    
    
    @Bean
    public RPCClient rpcClient() {
        log.info("properties:{}", properties);
        LoadBalancer loadBalancer = applicationContext.getBean(properties.getLoadBalanceStrategy().toUpperCase(), LoadBalancer.class);
        ServiceDiscovery discovery = new ServiceDiscovery(properties.getRegistryAddress(), loadBalancer);
        CLIENT.setDiscovery(discovery);
        CLIENT.init();
        return CLIENT;
    }

    /**
     * 因为RPCProxyFactoryBeanRegistry初始化是在常规bean还没有初始化之前进行的，所以是拿不到@Autowired的属性的
     * 只能去直接读配置文件才能得到basePackage
     *
     * @return
     */
    @Bean
    public static RPCConsumerProxyFactoryBeanRegistry rpcConsumerProxyFactoryBeanRegistry() {
        CLIENT = new RPCClient();
        String basePackage = PropertyUtil.getProperty("rpc.serviceBasePackage");
        return new RPCConsumerProxyFactoryBeanRegistry(CLIENT, basePackage);
    }
}
