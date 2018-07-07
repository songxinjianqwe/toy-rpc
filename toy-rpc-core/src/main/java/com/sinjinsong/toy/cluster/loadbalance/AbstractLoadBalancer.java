package com.sinjinsong.toy.core.cluster.loadbalance;

import com.sinjinsong.rpc.core.cluster.LoadBalancer;
import com.sinjinsong.rpc.core.registry.ServiceDiscovery;
import com.sinjinsong.rpc.core.transport.client.endpoint.Endpoint;
import com.sinjinsong.rpc.core.transport.domain.RPCRequest;
import com.sinjinsong.toy.core.registry.ServiceDiscovery;
import com.sinjinsong.toy.core.transport.client.endpoint.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {
    @Autowired
    private ServiceDiscovery serviceDiscovery;
    private Map<String, Endpoint> endpoints = new ConcurrentHashMap<>();
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(100, 100, 6L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    
    public AbstractLoadBalancer(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }
    
    @Override
    public Endpoint select(RPCRequest request) {
        List<String> newAddresses = serviceDiscovery.discover();
        Set<String> oldAddresses = endpoints.keySet();

        Set<String> intersect = new HashSet<>(newAddresses);
        intersect.retainAll(oldAddresses);
        
        for (String address : oldAddresses) {
            if (!intersect.contains(address)) {
                endpoints.remove(address);
            }
        }
        
        for (String address : newAddresses) {
            if (!intersect.contains(address)) {
                endpoints.put(address, new Endpoint(address,pool));
            }
        }
        return doSelect(new ArrayList<>(endpoints.values()), request);
    }
    
    abstract protected Endpoint doSelect(List<Endpoint> endpoints, RPCRequest request);

    @Override
    public void close() {
        pool.shutdown();
        endpoints.values().forEach(endpoint -> endpoint.close());
    }
}
