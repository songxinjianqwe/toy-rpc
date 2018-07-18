package com.sinjinsong.toy.protocol.api;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.transport.api.Endpoint;

import java.util.concurrent.ExecutorService;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Protocol {
    /**
     * 暴露服务
     *
     * @param invoker
     * @param <T>
     * @return
     * @throws RPCException
     */
    <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException;

    /**
     * 引用服务
     *
     * @param type
     * @param <T>
     * @return
     * @throws RPCException
     */
    <T> Invoker<T> refer(Class<T> type) throws RPCException;

    <T> ServiceConfig<T> getExportedServiceConfig(String interfaceMame) throws RPCException;
    
    Endpoint openClient(String interfaceName, String address, ExecutorService callbackPool, Serializer serializer);
    
    void openServer(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registry,
                    ProtocolConfig protocolConfig);
} 
