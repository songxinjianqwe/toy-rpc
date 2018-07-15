package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.Protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public abstract class AbstractProtocol implements Protocol {
    private Map<String, Invoker<?>> invokers = new ConcurrentHashMap<>();
    private Map<String, Exporter<?>> exporters = new ConcurrentHashMap<>();
    
    protected void putInvoker(Class<?> interfaceClass,Invoker<?> invoker) {
        this.invokers.put(interfaceClass.getName(),invoker);
    }
    
    protected void putExporter(Class<?> interfaceClass,Exporter<?> exporter) {
        this.exporters.put(interfaceClass.getName(),exporter);
    }

    @Override
    public <T> ServiceConfig<T> getExportedServiceConfig(String interfaceMame) throws RPCException {
        if(!exporters.containsKey(interfaceMame)){
            throw new RPCException("未找到暴露的服务" + interfaceMame);
        }
        return (ServiceConfig<T>) exporters.get(interfaceMame).getServiceConfig();
    }
}
