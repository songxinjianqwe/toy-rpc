package com.sinjinsong.toy.protocol.api;


import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Invoker<T> {

    Class<T> getInterface();

    String getInterfaceName();
    
    RPCResponse invoke(InvokeParam invokeParam) throws RPCException;
    
    void close();

    /**
     * 本地服务返回本地IP地址，参数为空；集群服务抛出异常；远程服务返回注册中心中的ServiceURL
     * @return
     */
    ServiceURL getServiceURL();
}
