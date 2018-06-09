package com.sinjinsong.rpc.core.client.proxy;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */

import com.sinjinsong.rpc.core.client.RPCClient;
import com.sinjinsong.rpc.core.domain.RPCRequest;
import com.sinjinsong.rpc.core.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@Slf4j
public class RPCConsumerProxyFactoryBean implements FactoryBean<Object>, InitializingBean {
    private RPCClient client;
    private Class<?> interfaceClass;
    private Object proxy;

    @Override
    public Object getObject() throws Exception {
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
        this.proxy = Proxy.newProxyInstance(
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
                        CompletableFuture<RPCResponse> responseFuture = client.execute(request);
                        RPCResponse response = responseFuture.get();
                        log.info("客户端读到响应");
                        if (response.hasError()) {
                            throw response.getCause();
                        } else {
                            return response.getResult();
                        }
                    }
                }
        );
    }

    public void setClient(RPCClient client) {
        this.client = client;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
}

