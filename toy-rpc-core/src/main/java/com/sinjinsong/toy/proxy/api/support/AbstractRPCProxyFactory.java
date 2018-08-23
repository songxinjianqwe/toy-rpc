package com.sinjinsong.toy.proxy.api.support;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractInvoker;
import com.sinjinsong.toy.protocol.api.support.RPCInvokeParam;
import com.sinjinsong.toy.proxy.api.RPCProxyFactory;
import com.sinjinsong.toy.transport.api.domain.GlobalRecycler;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
@Slf4j
public abstract class AbstractRPCProxyFactory implements RPCProxyFactory {
    protected Map<Class<?>, Object> cache = new ConcurrentHashMap<>();
    

    @Override
    public <T> T createProxy(Invoker<T> invoker) {
        if (cache.containsKey(invoker.getInterface())) {
            return (T) cache.get(invoker.getInterface());
        }
        T t = doCreateProxy(invoker.getInterface(), invoker);
        cache.put(invoker.getInterface(), t);
        return t;
    }

    protected abstract <T> T doCreateProxy(Class<T> interfaceClass, Invoker<T> invoker);


    protected Object invokeProxyMethod(Invoker invoker, Method method, Object[] args) {
        // 创建并初始化 RPC 请求
        if ("toString".equals(method.getName()) && method.getParameterTypes().length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(method.getName()) && method.getParameterTypes().length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(method.getName()) && method.getParameterTypes().length == 1) {
            return invoker.equals(args[0]);
        }
        // 复用request
        RPCRequest request = GlobalRecycler.reuse(RPCRequest.class);
        log.info("调用服务：{} {}", method.getDeclaringClass().getName(), method.getName());
        request.setRequestId(UUID.randomUUID().toString());
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
        // ClusterInvoker
        RPCInvokeParam invokeParam = RPCInvokeParam.builder()
                .rpcRequest(request)
                .referenceConfig(ReferenceConfig.getReferenceConfigByInterfaceName(method.getDeclaringClass().getName()))
                .build();
        RPCResponse response = invoker.invoke(invokeParam);
        Object result = null;
        // result == null when callback,oneway,async
        if (response != null) {
            result = response.getResult();
        }
        // 回收response
        response.recycle();
        return result;
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type) {
        return new AbstractInvoker<T>() {
            @Override
            public Class<T> getInterface() {
                return type;
            }

            @Override
            public String getInterfaceName() {
                return type.getName();
            }

            @Override
            public RPCResponse invoke(InvokeParam invokeParam) throws RPCException {
                RPCResponse response = GlobalRecycler.reuse(RPCResponse.class);
                try {
                    Method method = proxy.getClass().getMethod(invokeParam.getMethodName(), invokeParam.getParameterTypes());
                    response.setRequestId(invokeParam.getRequestId());
                    response.setResult(method.invoke(proxy, invokeParam.getParameters()));
                } catch (Exception e) {
                    response.setCause(e);
                }
                return response;
            }
        };
    }
}
