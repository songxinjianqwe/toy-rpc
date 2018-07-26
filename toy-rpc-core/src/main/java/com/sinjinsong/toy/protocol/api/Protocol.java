package com.sinjinsong.toy.protocol.api;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.registry.api.ServiceURL;

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
     * @param referenceConfig
     * @param <T>
     * @return
     * @throws RPCException
     */
    <T> Invoker<T> refer(ReferenceConfig<T> referenceConfig,ServiceURL serviceURL) throws RPCException;

    /**
     * 查找暴露的服务
     *
     * @param interfaceMame
     * @param <T>
     * @return
     * @throws RPCException
     */
    <T> ServiceConfig<T> referLocalService(String interfaceMame) throws RPCException;
    
    void close();
} 
