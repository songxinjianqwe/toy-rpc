package com.sinjinsong.toy.proxy.api.support;

import com.sinjinsong.toy.common.domain.GlobalRecycler;
import com.sinjinsong.toy.common.domain.RPCRequest;
import com.sinjinsong.toy.common.domain.RPCResponse;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractInvoker;
import com.sinjinsong.toy.protocol.api.support.RPCInvokeParam;
import com.sinjinsong.toy.proxy.api.RPCProxyFactory;
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
    
    public Object invokeProxyMethod(Invoker invoker, String interfaceName, String methodName, String[] parameterTypes, Object[] args) {
        // 创建并初始化 RPC 请求
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
        // 复用request
        RPCRequest request = GlobalRecycler.reuse(RPCRequest.class);
        log.info("调用服务：{}#{},parameterTypes:{},args:{}", interfaceName, methodName,parameterTypes,args);
        request.setRequestId(UUID.randomUUID().toString());
        request.setInterfaceName(interfaceName);
        request.setMethodName(methodName);
        request.setParameterTypes(parameterTypes);
        request.setParameters(args);
        // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
        // ClusterInvoker
        RPCInvokeParam invokeParam = RPCInvokeParam.builder()
                .rpcRequest(request)
                .referenceConfig(ReferenceConfig.getReferenceConfigByInterfaceName(interfaceName))
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

    protected Object invokeProxyMethod(Invoker invoker, Method method, Object[] args) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        String[] paramTypes = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            paramTypes[i] = parameterTypes[i].getName();
        };
        return invokeProxyMethod(invoker,method.getDeclaringClass().getName(),method.getName(),paramTypes,args);
    }

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
