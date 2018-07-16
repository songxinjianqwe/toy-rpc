package com.sinjinsong.toy.proxy;

import com.sinjinsong.toy.cluster.ClusterInvoker;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.invoke.api.Invocation;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractInvoker;
import com.sinjinsong.toy.proxy.api.support.AbstractRPCProxyFactory;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;
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

    @Override
    protected <T> T doCreateProxy(Class<T> interfaceClass, ClusterInvoker<T> invoker) {
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
                        // ClusterInvoker
                        RPCResponse response = invoker.invoke(request,ReferenceConfig.getReferenceConfigByInterface(method.getDeclaringClass()));
                        if(response == null) {
                            return null;
                        }
                        if (response.hasError()) {
                            throw new RPCException("invocation failed", response.getCause());
                        } else {
                            return response.getResult();
                        }
                    }
                }
        );
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type) {
        return new AbstractInvoker<T>() {
            @Override
            public Class<T> getInterface() {
                return type;
            }
            
            @Override
            protected RPCResponse doInvoke(Invocation invocation) throws RPCException {
                 RPCResponse response = new RPCResponse();
                try {
                    Method method = proxy.getClass().getMethod(invocation.getMethodName(),invocation.getParameterTypes());
                    response.setRequestId(invocation.getRequestId());
                    response.setResult(method.invoke(proxy, invocation.getParameters()));
                } catch (Exception e) {
                    response.setCause(e);
                }
                return response;
            }
        };
    }
}

