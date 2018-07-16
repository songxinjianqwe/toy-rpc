package com.sinjinsong.toy.cluster.support;


import com.sinjinsong.toy.cluster.ClusterInvoker;
import com.sinjinsong.toy.cluster.LoadBalancer;
import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.config.ClusterConfig;
import com.sinjinsong.toy.config.ProtocolConfig;
import com.sinjinsong.toy.config.RegistryConfig;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
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
     * 客户端callback线程池
     */
    private ExecutorService callbackPool = Executors.newSingleThreadExecutor();

    /**
     * 分配address的形式
     *
     * @param interfaceClass
     * @param <T>
     * @return
     */
    @Override
    public <T> ClusterInvoker<T> register(Class<T> interfaceClass) {
        String interfaceName = interfaceClass.getName();
        ClusterInvoker clusterInvoker;
        if (!interfaceInvokers.containsKey(interfaceName)) {
            clusterInvoker = new ClusterInvoker(interfaceClass,applicationConfig,clusterConfig,registryConfig,protocolConfig,callbackPool);
            interfaceInvokers.put(interfaceName,clusterInvoker);
            return clusterInvoker;
        }
        return interfaceInvokers.get(interfaceName);
    }

    @Override
    public Invoker select(RPCRequest request) {
        // 调整endpoint，如果某个服务器不提供该服务了，则看它是否还提供其他服务，如果都不提供了，则关闭连接
        // 如果某个服务器还没有连接，则连接；如果已经连接，则复用
        ClusterInvoker clusterInvoker = interfaceInvokers.get(request.getInterfaceName());
        return doSelect(clusterInvoker.getInvokers(), request);
    }
    
    
    protected abstract Invoker doSelect(List<Invoker> invokers, RPCRequest request);

    @Override
    public void close() {
        callbackPool.shutdown();
        interfaceInvokers.values().forEach(clusterInvoker -> clusterInvoker.close());
    }


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
