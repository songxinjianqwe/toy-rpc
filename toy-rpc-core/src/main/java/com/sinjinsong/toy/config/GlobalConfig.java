package com.sinjinsong.toy.config;

import com.sinjinsong.toy.cluster.FaultToleranceHandler;
import com.sinjinsong.toy.cluster.LoadBalancer;
import com.sinjinsong.toy.executor.api.TaskExecutor;
import com.sinjinsong.toy.protocol.api.Protocol;
import com.sinjinsong.toy.proxy.api.RPCProxyFactory;
import com.sinjinsong.toy.registry.api.ServiceRegistry;
import com.sinjinsong.toy.serialize.api.Serializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sinjinsong
 * @date 2018/7/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalConfig {
    private ApplicationConfig applicationConfig;
    private ClusterConfig clusterConfig;
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    
    public Serializer getSerializer() {
        return applicationConfig.getSerializerInstance();
    }
    
    public RPCProxyFactory getProxyFactory() {
        return applicationConfig.getProxyFactoryInstance();
    }
    
    
    public LoadBalancer getLoadBalancer() {
        return clusterConfig.getLoadBalanceInstance();
    }
    
    public FaultToleranceHandler getFaultToleranceHandler() {
        return clusterConfig.getFaultToleranceHandlerInstance();
    }
    
    public ServiceRegistry getServiceRegistry() {
        return registryConfig.getRegistryInstance();
    }
    
    
    public Protocol getProtocol() {
        return protocolConfig.getProtocolInstance();
    }
    
    public TaskExecutor getClientExecutor() {
        return protocolConfig.getExecutor().getClient().getExecutorInstance();
    }
    
    public TaskExecutor getServerExecutor() {
        return protocolConfig.getExecutor().getServer().getExecutorInstance();
    }
    
    public int getPort() {
        return protocolConfig.getPort();
    }
}
