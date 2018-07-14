package com.sinjinsong.toy.cluster.support;


import com.sinjinsong.toy.cluster.LoadBalancer;
import com.sinjinsong.toy.config.RegistryConfig;
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
    private Map<String, Map<String, Endpoint>> interfaceEndpoints = new ConcurrentHashMap<>();
    /**
     * 客户端callback线程池
     */
    private ExecutorService callbackPool = Executors.newSingleThreadExecutor();
    private Serializer serializer;
        
    @Override
    public Endpoint select(RPCRequest request) {
        // 调整endpoint，如果某个服务器不提供该服务了，则看它是否还提供其他服务，如果都不提供了，则关闭连接
        // 如果某个服务器还没有连接，则连接；如果已经连接，则复用
        List<String> newAddresses = registryConfig.getRegistryInstance().discover(request.getInterfaceName());
        if (!interfaceEndpoints.containsKey(request.getInterfaceName())) {
            interfaceEndpoints.put(request.getInterfaceName(), new ConcurrentHashMap<>());
        }
        Map<String, Endpoint> addressEndpoints = interfaceEndpoints.get(request.getInterfaceName());
        Set<String> oldAddresses = addressEndpoints.keySet();
        
        Set<String> intersect = new HashSet<>(newAddresses);
        intersect.retainAll(oldAddresses);
        
        for (String address : oldAddresses) {
            if (!intersect.contains(address)) {
                // 去掉一个Endpoint所管理的一个服务，如果某个Endpoint不管理任何服务，则关闭连接
                addressEndpoints.get(address).closeIfNoServiceAvailable(request.getInterfaceName());
                addressEndpoints.remove(address);
            }
        }

        for (String address : newAddresses) {
            if (!intersect.contains(address)) {
                Endpoint endpoint = null;
                // 找其他服务的Map中是否包含该endpoint，如果包含，则说明已经连接了，复用该连接即可
                for (Map<String, Endpoint> map : interfaceEndpoints.values()) {
                    if (map != addressEndpoints && map.containsKey(address)) {
                        endpoint = map.get(address);
                        endpoint.addInterface(request.getInterfaceName());
                    }
                }
                // 如果不包含，则建立连接
                addressEndpoints.put(address, endpoint != null ? endpoint : new Endpoint(address, callbackPool, request.getInterfaceName(),serializer));
            }
        }

        return doSelect(new ArrayList<>(addressEndpoints.values()), request);
    }

    protected abstract Endpoint doSelect(List<Endpoint> endpoints, RPCRequest request);

    @Override
    public void close() {
        callbackPool.shutdown();
        interfaceEndpoints.forEach( (interfaceName,map) -> map.values().forEach(endpoint -> endpoint.closeIfNoServiceAvailable(interfaceName)));
    }

    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }
}
