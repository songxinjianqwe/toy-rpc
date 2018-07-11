package com.sinjinsong.toy.rpc.api;

import com.sinjinsong.toy.common.exception.RPCException;

import java.net.URL;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Protocol {
    /**
     * 暴露服务
     * @param invoker
     * @param <T>
     * @return
     * @throws RPCException
     */
    <T> Exporter<T> export(Invoker<T> invoker) throws RPCException;
    
    /**
     * 引用服务
     * @param type
     * @param url
     * @param <T>
     * @return
     * @throws RPCException
     */
    <T> Invoker<T> refer(Class<T> type, URL url) throws RPCException;
}
