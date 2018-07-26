package com.sinjinsong.toy.config.generic;

import com.sinjinsong.toy.common.ExtensionLoader;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.filter.Filter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/23
 */
@Slf4j
public class RPCGenericServiceBean {
    private ReferenceConfig referenceConfig;

    public void init(String interfaceName, boolean isAsync, boolean isCallback, boolean isOneway, int timeout, String callbackMethod, int callbackParamIndex,
                     ApplicationConfig applicationConfig, ClusterConfig clusterConfig, ProtocolConfig protocolConfig, RegistryConfig registryConfig) {
        referenceConfig = ReferenceConfig.createReferenceConfig(
                interfaceName,
                null,
                isAsync,
                isCallback,
                isOneway,
                timeout,
                callbackMethod,
                callbackParamIndex,
                true,
                ExtensionLoader.getInstance().load(Filter.class)
        );
        referenceConfig.init(
                applicationConfig,
                clusterConfig,
                protocolConfig,
                registryConfig
        );
    }
    
    public Object invoke(String methodName, Class<?>[] parameterTypes, Object[] parameters){
        return  referenceConfig.invokeForGeneric(methodName,parameterTypes,parameters);  
    }


}
