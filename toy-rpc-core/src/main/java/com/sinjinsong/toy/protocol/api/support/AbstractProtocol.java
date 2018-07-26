package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Protocol;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
@Slf4j
public abstract class AbstractProtocol implements Protocol {
    private Map<String, Exporter<?>> exporters = new ConcurrentHashMap<>();
    private ProtocolConfig protocolConfig;
    private ApplicationConfig applicationConfig;
    private ClusterConfig clusterConfig;
    private RegistryConfig registryConfig;
    
    
    public void init(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registryConfig, ProtocolConfig protocolConfig) {
        this.applicationConfig = applicationConfig;
        this.protocolConfig = protocolConfig;
        this.clusterConfig = clusterConfig;
        this.registryConfig = registryConfig;
    }

    protected ProtocolConfig getProtocolConfig() {
        return protocolConfig;
    }

    protected ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    protected ClusterConfig getClusterConfig() {
        return clusterConfig;
    }

    protected RegistryConfig getRegistryConfig() {
        return registryConfig;
    }
    
    protected void putExporter(Class<?> interfaceClass, Exporter<?> exporter) {
        this.exporters.put(interfaceClass.getName(), exporter);
    }

    @Override
    public <T> ServiceConfig<T> referLocalService(String interfaceMame) throws RPCException {
        if (!exporters.containsKey(interfaceMame)) {
            throw new RPCException(ErrorEnum.EXPOSED_SERVICE_NOT_FOUND,"未找到暴露的服务:{}", interfaceMame);
        }
        return (ServiceConfig<T>) exporters.get(interfaceMame).getServiceConfig();
    }

    @Override
    public void close() {
        
    }
}
