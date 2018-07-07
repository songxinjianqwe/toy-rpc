package com.sinjinsong.toy.core.proxy;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */

import com.sinjinsong.rpc.core.config.ReferenceConfig;
import com.sinjinsong.rpc.core.exchange.ExchangeHandler;
import com.sinjinsong.rpc.core.transport.client.RPCClient;
import com.sinjinsong.rpc.core.transport.domain.RPCRequest;
import com.sinjinsong.toy.core.config.ReferenceConfig;
import com.sinjinsong.toy.core.transport.client.RPCClient;
import com.sinjinsong.toy.core.transport.domain.RPCRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@Slf4j
@Setter
public class RPCConsumerProxyFactoryBean<T> implements FactoryBean<T>, InitializingBean {
    private RPCClient client;
    private Class<T> interfaceClass;
    private T proxy;
    private ExchangeHandler exchangeHandler;
    private ReferenceConfig<T> referenceConfig;
    
    @Override
    public T getObject() throws Exception {
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return this.interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.proxy = (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 创建并初始化 RPC 请求
                        RPCRequest request = new RPCRequest();
                        log.info("调用远程服务：{} {}", method.getDeclaringClass().getName(), method.getName());
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
                        return exchangeHandler.handleExchange(request,referenceConfig);
                    }
                }
        );
    }

}

