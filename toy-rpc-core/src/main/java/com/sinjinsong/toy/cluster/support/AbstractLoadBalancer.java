package com.sinjinsong.toy.cluster.support;


import com.sinjinsong.toy.cluster.ClusterInvoker;
import com.sinjinsong.toy.cluster.LoadBalancer;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sinjinsong
 * @date 2018/6/10
 * 
 * invoker是对应一个interface的一个address
 * endpoint是对应一个address
 * 多个invoker可能会共享同一个endpoint
 * Endpoint由Protocol管理
 */
@Slf4j
public abstract class AbstractLoadBalancer implements LoadBalancer {
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    private ApplicationConfig applicationConfig;
    private ClusterConfig clusterConfig;

    /**
     * key是接口名，value的key是IP地址，value是Endpoint
     * <p>
     * 一种可能的格式：
     * key : AService, value:   192.168.1.1,Endpoint1
     * 192.168.1.2,Endpoint2
     * key : BService, value:   192.168.1.1,Endpoint1
     */
    private Map<String, ClusterInvoker> interfaceInvokers = new ConcurrentHashMap<>();
    
    /**
     * 分配address的形式
     *
     * @param referenceConfig
     * @param <T>
     * @return
     */
    @Override
    public <T> Invoker<T> referCluster(ReferenceConfig<T> referenceConfig) {
        String interfaceName = referenceConfig.getInterfaceName();
        ClusterInvoker clusterInvoker;
        if (!interfaceInvokers.containsKey(interfaceName)) {
            clusterInvoker = new ClusterInvoker(referenceConfig.getInterfaceClass(), interfaceName, applicationConfig, clusterConfig, registryConfig, protocolConfig);
            interfaceInvokers.put(interfaceName, clusterInvoker);
            return clusterInvoker;
        }
        return interfaceInvokers.get(interfaceName);
    }

    @Override
    public Invoker select(List<Invoker> invokers, RPCRequest request) {
        if (invokers.size() == 0) {
            log.info("select->不存在可用invoker，直接退出");
            return null;
        }
        // 调整endpoint，如果某个服务器不提供该服务了，则看它是否还提供其他服务，如果都不提供了，则关闭连接
        // 如果某个服务器还没有连接，则连接；如果已经连接，则复用
        Invoker invoker = doSelect(invokers, request);
        log.info("LoadBalance:{},chosen invoker:{},requestId:" + request.getRequestId(), this.getClass().getSimpleName(), invoker.getServiceURL());
        return invoker;
    }


    protected abstract Invoker doSelect(List<Invoker> invokers, RPCRequest request);
    
    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    public void setProtocolConfig(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public void setClusterConfig(ClusterConfig clusterConfig) {
        this.clusterConfig = clusterConfig;
    }
}
