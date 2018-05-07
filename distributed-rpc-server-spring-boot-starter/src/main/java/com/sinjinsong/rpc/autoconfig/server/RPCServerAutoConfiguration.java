package com.sinjinsong.rpc.autoconfig.server;


import com.sinjinsong.rpc.core.server.RPCServer;
import com.sinjinsong.rpc.core.zk.ServiceRegistry;
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
@EnableConfigurationProperties(RPCServerProperties.class)
@ConditionalOnMissingBean(RPCServer.class)
@Slf4j
public class RPCServerAutoConfiguration {
    @Autowired
    private RPCServerProperties properties;
    
    
    @Bean
    public RPCServer rpcServer() {
        log.info("开始初始化RPCServer");
        log.info("配置文件读取结果:{}",properties);
        ServiceRegistry registry = new ServiceRegistry(properties.getRegistryAddress());
        return new RPCServer(properties.getServiceBasePackage(),  registry);
    }
    
}
