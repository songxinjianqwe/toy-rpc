package com.sinjinsong.toy.cluster.support;


import com.sinjinsong.toy.cluster.ClusterInvoker;
import com.sinjinsong.toy.cluster.LoadBalancer;
import com.sinjinsong.toy.config.ClusterConfig;
import com.sinjinsong.toy.config.RegistryConfig;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractInvoker;
import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.transport.client.Endpoint;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {
    private RegistryConfig registryConfig;
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
    private Serializer serializer;


    /**
     * 分配address的形式
     *
     * @param invoker
     * @param clusterConfig
     * @param <T>
     * @return
     */
    @Override
    public <T> Invoker<T> register(Invoker<T> invoker, ClusterConfig clusterConfig) {
        AbstractInvoker atomicInvoker = (AbstractInvoker) invoker;
        String interfaceName = invoker.getInterface().getName();
        ClusterInvoker clusterInvoker;
        List<String> newAddresses = registryConfig.getRegistryInstance().discover(interfaceName);
        if (!interfaceInvokers.containsKey(invoker.getInterface())) {
            clusterInvoker = new ClusterInvoker();
            clusterInvoker.setInterfaceClass(invoker.getInterface());
            clusterInvoker.setClusterConfig(clusterConfig);
            Endpoint endpoint = new Endpoint(newAddresses.get(0), callbackPool, interfaceName, serializer);
            atomicInvoker.setEndpoint(endpoint);
            return clusterInvoker;
        }
        clusterInvoker = interfaceInvokers.get(invoker.getInterface());
        
        clusterInvoker.refresh(newAddresses);
        
        for (String newAddress : newAddresses) {
            if (!clusterInvoker.containsAddress(newAddress)) {
                Endpoint endpoint = new Endpoint(newAddresses.get(0), callbackPool, interfaceName, serializer);
                atomicInvoker.setEndpoint(endpoint);
                break;
            }
        }
        return clusterInvoker;
    }

    @Override
    public Invoker select(RPCRequest request) {
        // 调整endpoint，如果某个服务器不提供该服务了，则看它是否还提供其他服务，如果都不提供了，则关闭连接
        // 如果某个服务器还没有连接，则连接；如果已经连接，则复用
        ClusterInvoker clusterInvoker = interfaceInvokers.get(request.getInterfaceName());
        clusterInvoker.refresh(registryConfig.getRegistryInstance().discover(request.getInterfaceName()));
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

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }
}
