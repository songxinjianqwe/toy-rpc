package com.sinjinsong.toy.proxy;

import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.proxy.api.support.AbstractRPCProxyFactory;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@Slf4j
public class JDKRPCProxyFactory extends AbstractRPCProxyFactory {
        
    public <T> T createProxy(Invoker<T> invoker) {
        return (T) Proxy.newProxyInstance(
                invoker.getInterface().getClassLoader(),
                new Class<?>[]{invoker.getInterface()},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 创建并初始化 RPC 请求
                        RPCRequest request = new RPCRequest();
                        log.info("调用远程服务：{} {}", method.getDeclaringClass().getName(), method.getName());
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setInterfaceName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
                        return invoker.invoke(request);
                    }
                }
        );
    }
}

