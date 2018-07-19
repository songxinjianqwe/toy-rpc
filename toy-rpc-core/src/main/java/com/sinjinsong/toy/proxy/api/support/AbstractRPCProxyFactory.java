package com.sinjinsong.toy.proxy.api.support;

import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.proxy.api.RPCProxyFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public abstract class AbstractRPCProxyFactory implements RPCProxyFactory {
    protected Map<Class<?>, Object> cache = new ConcurrentHashMap<>();
        
    @Override
    public <T> T createProxy(Invoker<T> invoker) {
        if (cache.containsKey(invoker.getInterface())) {
            return (T) cache.get(invoker.getInterface());
        }
        T t = doCreateProxy(invoker.getInterface(),invoker);
        cache.put(invoker.getInterface(),t);
        return t;
    }

    protected abstract <T> T doCreateProxy(Class<T> interfaceClass,Invoker<T> invoker);
}
