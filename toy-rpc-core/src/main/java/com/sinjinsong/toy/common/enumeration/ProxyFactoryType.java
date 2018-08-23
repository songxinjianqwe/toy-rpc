package com.sinjinsong.toy.common.enumeration;

import com.sinjinsong.toy.common.enumeration.support.ExtensionBaseType;
import com.sinjinsong.toy.proxy.JavassistRPCProxyFactory;
import com.sinjinsong.toy.proxy.JdkRPCProxyFactory;
import com.sinjinsong.toy.proxy.api.RPCProxyFactory;

/**
 * @author sinjinsong
 * @date 2018/8/23
 */
public enum ProxyFactoryType implements ExtensionBaseType<RPCProxyFactory> {
    JAVASSIST(new JavassistRPCProxyFactory()),JDK(new JdkRPCProxyFactory());
    private RPCProxyFactory proxyFactory;
    
    ProxyFactoryType(RPCProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }
    
    @Override
    public RPCProxyFactory getInstance() {
        return proxyFactory;
    }
}