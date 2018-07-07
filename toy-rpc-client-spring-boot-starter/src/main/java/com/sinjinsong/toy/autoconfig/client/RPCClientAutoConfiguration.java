package com.sinjinsong.toy.autoconfig.client;


import com.sinjinsong.toy.cluster.LoadBalancer;
import com.sinjinsong.toy.cluster.loadbalance.ConsistentHashLoadBalancer;
import com.sinjinsong.toy.cluster.loadbalance.LeastActiveLoadBalancer;
import com.sinjinsong.toy.cluster.loadbalance.RandomLoadBalancer;
import com.sinjinsong.toy.cluster.loadbalance.RoundRobinLoadBalancer;
import com.sinjinsong.toy.common.util.PropertyUtil;
import com.sinjinsong.toy.proxy.RPCConsumerProxyFactoryBeanRegistry;
import com.sinjinsong.toy.registry.ServiceDiscovery;
import com.sinjinsong.toy.remoting.transport.client.RPCClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    @Bean
    public ServiceDiscovery serviceDiscovery() {
        return new ServiceDiscovery(properties.getRegistryAddress());
    }
    
    @ConditionalOnProperty(value = "rpc.loadBalanceStrategy", havingValue = "CONSISTENT_HASH")
    @Bean(name = "CONSISTENT_HASH")
    public ConsistentHashLoadBalancer consistentHashLoadBalancer() {
        return new ConsistentHashLoadBalancer(applicationContext.getBean(ServiceDiscovery.class));
    }

    @ConditionalOnProperty(value = "rpc.loadBalanceStrategy",havingValue = "RANDOM")
    @Bean(name = "RANDOM")
    public RandomLoadBalancer randomLoadBalancer() {
        return new RandomLoadBalancer(applicationContext.getBean(ServiceDiscovery.class));
    }

    @ConditionalOnProperty(value = "rpc.loadBalanceStrategy", havingValue = "ROUND_ROBIN")
    @Bean(name = "ROUND_ROBIN")
    public RoundRobinLoadBalancer roundRobinLoadBalancer() {
        return new RoundRobinLoadBalancer(applicationContext.getBean(ServiceDiscovery.class));
    }

    @ConditionalOnProperty(value = "rpc.loadBalanceStrategy", havingValue = "LEAST_ACTIVE")
    @Bean(name = "LEAST_ACTIVE")
    public LeastActiveLoadBalancer leastActiveLoadBalancer() {
        return new LeastActiveLoadBalancer(applicationContext.getBean(ServiceDiscovery.class));
    }
    
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
