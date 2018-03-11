package com.sinjinsong.rpc.autoconfig.client;


import com.sinjinsong.rpc.core.client.RPCClient;
import com.sinjinsong.rpc.core.proxy.RPCProxyFactoryBeanRegistry;
import com.sinjinsong.rpc.core.util.PropertyUtil;
import com.sinjinsong.rpc.core.zookeeper.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@Configuration
@EnableConfigurationProperties(RPCClientProperties.class)
@ConditionalOnMissingBean(RPCProxyFactoryBeanRegistry.class)
@Slf4j
public class RPCClientAutoConfiguration {
    @Autowired
    private RPCClientProperties properties;
    private static RPCClient CLIENT;
    
    @Bean
    public RPCClient rpcClient() {
        log.info("properties:{}", properties);
        ServiceDiscovery discovery = new ServiceDiscovery(properties.getRegistryAddress());
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
    public static RPCProxyFactoryBeanRegistry rpcProxyFactoryBeanRegistry(){
        CLIENT = new RPCClient();
        String basePackage = PropertyUtil.getProperty("rpc.serviceBasePackage");
        return new RPCProxyFactoryBeanRegistry(CLIENT,basePackage);    
    }    
}
