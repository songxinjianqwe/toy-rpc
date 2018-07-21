package com.sinjinsong.toy.protocol.api;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.*;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Protocol {
    /**
     * 应用初始化完后调用
     * @param applicationConfig
     * @param clusterConfig
     * @param registry
     * @param protocolConfig
     */
    void doOnApplicationLoadComplete(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registry, ProtocolConfig protocolConfig);
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

    /**
     * 查找暴露的服务
     * @param interfaceMame
     * @param <T>
     * @return
     * @throws RPCException
     */
    <T> ServiceConfig<T> referLocalService(String interfaceMame) throws RPCException;
} 
