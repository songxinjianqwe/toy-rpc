package com.sinjinsong.toy.proxy;

import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.proxy.api.support.AbstractRPCProxyFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@Slf4j
public class JdkRPCProxyFactory extends AbstractRPCProxyFactory {
    
    @Override
    protected <T> T doCreateProxy(Class<T> interfaceClass, Invoker<T> invoker) {
        return (T) Proxy.newProxyInstance(
                invoker.getInterface().getClassLoader(),
                new Class<?>[]{invoker.getInterface()},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                       return JdkRPCProxyFactory.this.invokeProxyMethod(invoker,method,args);
                    }
                }
        );
    }
}

