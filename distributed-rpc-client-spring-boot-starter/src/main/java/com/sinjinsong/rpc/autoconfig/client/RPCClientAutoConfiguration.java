package com.sinjinsong.rpc.autoconfig.client;


import com.sinjinsong.rpc.core.client.RPCClient;
import com.sinjinsong.rpc.core.proxy.RPCProxyFactory;
import com.sinjinsong.rpc.core.zookeeper.ServiceDiscovery;
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
@ConditionalOnMissingBean(RPCClient.class)
@Slf4j
public class RPCClientAutoConfiguration {
    @Autowired
    private RPCClientProperties properties;
    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public RPCClient rpcClient() {
        log.info("properties:{}", properties);
        log.info("applicationContext:{}", applicationContext);
        ServiceDiscovery discovery = new ServiceDiscovery(properties.getRegistryAddress());
        
        RPCClient client = new RPCClient(discovery);
        RPCProxyFactory.init(applicationContext, client, properties.getServiceBasePackage());
        return client;
    }
}
