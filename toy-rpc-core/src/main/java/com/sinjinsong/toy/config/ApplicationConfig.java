package com.sinjinsong.toy.config;

import com.sinjinsong.toy.proxy.api.RPCProxyFactory;
import com.sinjinsong.toy.serialize.api.Serializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationConfig {
    private String name;
    private String serialize;
    private String proxy;
    
    private Serializer serializerInstance;
    private RPCProxyFactory proxyFactoryInstance;
    
}
