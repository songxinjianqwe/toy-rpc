package com.sinjinsong.toy.autoconfig;


import com.sinjinsong.toy.autoconfig.beanpostprocessor.RPCConsumerBeanPostProcessor;
import com.sinjinsong.toy.autoconfig.beanpostprocessor.RPCProviderBeanPostProcessor;
import com.sinjinsong.toy.cluster.FaultToleranceHandler;
import com.sinjinsong.toy.cluster.loadbalance.LeastActiveLoadBalancer;
import com.sinjinsong.toy.cluster.support.AbstractLoadBalancer;
import com.sinjinsong.toy.common.ExtensionLoader;
import com.sinjinsong.toy.common.enumeration.*;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.executor.api.TaskExecutor;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.filter.impl.ActiveLimitFilter;
import com.sinjinsong.toy.protocol.api.Protocol;
import com.sinjinsong.toy.proxy.JdkRPCProxyFactory;
import com.sinjinsong.toy.registry.zookeeper.ZkServiceRegistry;
import com.sinjinsong.toy.serialize.api.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@EnableConfigurationProperties(RPCProperties.class)
@Configuration
@Slf4j
public class RPCAutoConfiguration implements InitializingBean, ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {
    @Autowired
    private RPCProperties properties;
    private ApplicationContext ctx;
    private ExtensionLoader extensionLoader;


    @Bean(initMethod = "init", destroyMethod = "close")
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = properties.getRegistry();
        if (registryConfig == null) {
            throw new RPCException(ErrorEnum.APP_CONFIG_FILE_ERROR, "必须配置registry");
        }
        //TODO 根据type创建ServiceRegistry
        //TODO injvm协议不需要连接ZK
        ZkServiceRegistry serviceRegistry = new ZkServiceRegistry(registryConfig);
        registryConfig.setRegistryInstance(serviceRegistry);
        log.info("{}", registryConfig);
        return registryConfig;
    }

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig application = properties.getApplication();
        if (application == null) {
            throw new RPCException(ErrorEnum.APP_CONFIG_FILE_ERROR, "必须配置applicationConfig");
        }
        // TODO 根据类型创建proxyFactory和serializer
        application.setProxyFactoryInstance(new JdkRPCProxyFactory());

        application.setSerializerInstance(extensionLoader.load(Serializer.class, SerializerType.class, application.getSerialize()));
        log.info("{}", application);
        return application;
    }

    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = properties.getProtocol();
        if (protocolConfig == null) {
            throw new RPCException(ErrorEnum.APP_CONFIG_FILE_ERROR, "必须配置protocolConfig");
        }
        Protocol protocol = extensionLoader.load(Protocol.class, ProtocolType.class, protocolConfig.getType());
        protocolConfig.setProtocolInstance(protocol);

        ExecutorConfig executorConfig = protocolConfig.getExecutor();
        if (executorConfig != null) {
            TaskExecutor executor = extensionLoader.load(TaskExecutor.class, ExecutorType.class, executorConfig.getType());
            executor.init(executorConfig.getThreads());
            executorConfig.setExecutorInstance(executor);
        }
        log.info("{}", protocolConfig);


        return protocolConfig;
    }

    @Bean
    public ClusterConfig clusterconfig(RegistryConfig registryConfig, ProtocolConfig protocolConfig) {
        ClusterConfig clusterConfig = properties.getCluster();
        if (clusterConfig == null) {
            throw new RPCException(ErrorEnum.APP_CONFIG_FILE_ERROR, "必须配置clusterConfig");
        }
        AbstractLoadBalancer loadBalancer = extensionLoader.load(AbstractLoadBalancer.class, LoadBalanceType.class, clusterConfig.getLoadbalance());
        loadBalancer.setRegistryConfig(registryConfig);
        loadBalancer.setProtocolConfig(protocolConfig);
        loadBalancer.setClusterConfig(clusterConfig);
        loadBalancer.setApplicationConfig(applicationConfig());
        if (clusterConfig.getFaulttolerance() != null) {
            clusterConfig.setFaultToleranceHandlerInstance(extensionLoader.load(FaultToleranceHandler.class, FaultToleranceType.class, clusterConfig.getFaulttolerance()));
        } else {
            // 默认是failover
            clusterConfig.setFaultToleranceHandlerInstance(FaultToleranceType.FAILOVER.getInstance());
        }
        clusterConfig.setLoadBalanceInstance(loadBalancer);
        log.info("{}", clusterConfig);

        // 注册Filter
        if (loadBalancer instanceof LeastActiveLoadBalancer) {
            extensionLoader.register(Filter.class, "activeLimit", new ActiveLimitFilter());
        }
        return clusterConfig;
    }

    @Bean
    public RPCConsumerBeanPostProcessor rpcConsumerBeanPostProcessor(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, ProtocolConfig protocolConfig, RegistryConfig registryConfig) {
        RPCConsumerBeanPostProcessor processor = new RPCConsumerBeanPostProcessor();
        processor.init(applicationConfig, clusterConfig, protocolConfig, registryConfig);
        log.info("RPCConsumerBeanPostProcessor init");
        return processor;
    }

    @Bean
    public RPCProviderBeanPostProcessor rpcProviderBeanPostProcessor(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, ProtocolConfig protocolConfig, RegistryConfig registryConfig) {
        RPCProviderBeanPostProcessor processor = new RPCProviderBeanPostProcessor();
        processor.init(applicationConfig, clusterConfig, protocolConfig, registryConfig);
        log.info("RPCProviderBeanPostProcessor init");
        return processor;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Spring容器启动完毕");
        ctx.getBean(ProtocolConfig.class).getProtocolInstance().doOnApplicationLoadComplete(
                ctx.getBean(ApplicationConfig.class),
                ctx.getBean(ClusterConfig.class),
                ctx.getBean(RegistryConfig.class),
                ctx.getBean(ProtocolConfig.class)
        );
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 读取用户目录下的/toy下面的配置文件
        // 内部应用使用枚举单例的方式获取实例，外部应用使用Spring的方式获取实例
        this.extensionLoader = ExtensionLoader.getInstance();
        URL parent = this.getClass().getClassLoader().getResource("toy");
        if (parent != null) {
            log.info("/toy配置文件存在，开始读取...");
            File dir = new File(parent.getFile());
            File[] files = dir.listFiles();
            for (File file : files) {
                handleFile(file);
            }
            log.info("配置文件读取完毕!");
        }
    }

    private void handleFile(File file) {
        log.info("开始读取文件:{}",file);
        String interfaceName = file.getName();
        try {
            Class<?> interfaceClass = Class.forName(interfaceName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] kv = line.split("=");
                if (kv.length != 2) {
                    log.error("配置行不是x=y的格式的:{}",line);
                    throw new RPCException(ErrorEnum.EXTENSION_CONFIG_FILE_ERROR,"配置行不是x=y的格式的:{}",line);
                }
                // 如果有任何异常，则跳过这一行
                try {
                    Class<?> impl = Class.forName(kv[1]);
                    if (!interfaceClass.isAssignableFrom(impl)) {
                        log.error("实现类{}不是该接口{}的子类",impl,interfaceClass);
                        throw new RPCException(ErrorEnum.EXTENSION_CONFIG_FILE_ERROR,"实现类{}不是该接口{}的子类",impl,interfaceClass);
                    }
                    Object o = impl.newInstance();
                    extensionLoader.register(interfaceClass, kv[0], o);
                } catch (Throwable e) {
                    e.printStackTrace();
                    throw new RPCException(ErrorEnum.EXTENSION_CONFIG_FILE_ERROR,"实现类对象{}加载类或实例化失败",kv[1]);
                }
            }
            br.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RPCException(e,ErrorEnum.EXTENSION_CONFIG_FILE_ERROR,"接口对象{}加载类失败",file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RPCException(e,ErrorEnum.EXTENSION_CONFIG_FILE_ERROR,"配置文件{}读取失败",file.getName());
        }
    }
}


