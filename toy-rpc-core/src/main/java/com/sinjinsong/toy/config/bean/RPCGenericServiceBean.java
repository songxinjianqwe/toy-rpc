package com.sinjinsong.toy.config.bean;

import com.sinjinsong.toy.common.ExtensionLoader;
import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.filter.Filter;

import java.lang.reflect.Method;

/**
 * @author sinjinsong
 * @date 2018/7/23
 */
public class RPCGenericServiceBean {
    private Object proxy;
    
    public void init(String interfaceName, boolean isAsync, boolean isCallback, boolean isOneway, int timeout, String callbackMethod, int callbackParamIndex,
                     ApplicationConfig applicationConfig, ClusterConfig clusterConfig, ProtocolConfig protocolConfig, RegistryConfig registryConfig) {
        ReferenceConfig config = ReferenceConfig.createReferenceConfig(
                interfaceName,
                null,
                isAsync,
                isCallback,
                isOneway,
                timeout,
                callbackMethod,
                callbackParamIndex,
                ExtensionLoader.getInstance().load(Filter.class)
        );
        config.init(
                applicationConfig,
                clusterConfig,
                protocolConfig,
                registryConfig
        );
        proxy = config.get();
    }
    
    public Object invoke(String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        try {
            Method method = proxy.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(proxy, parameters);
        } catch (Throwable t){
            throw new RPCException(t,ErrorEnum.GENERIC_INVOCATION_ERROR,"泛化调用失败");
        }
    }
}
