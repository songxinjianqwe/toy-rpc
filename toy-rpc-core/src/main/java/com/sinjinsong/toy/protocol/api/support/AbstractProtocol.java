package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Protocol;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
@Slf4j
public abstract class AbstractProtocol implements Protocol {
    private Map<String, Exporter<?>> exporters = new ConcurrentHashMap<>();

    protected void putExporter(Class<?> interfaceClass, Exporter<?> exporter) {
        this.exporters.put(interfaceClass.getName(), exporter);
    }

    protected boolean isExporterExists() {
        return !exporters.isEmpty();
    }

    @Override
    public <T> ServiceConfig<T> referLocalService(String interfaceMame) throws RPCException {
        if (!exporters.containsKey(interfaceMame)) {
            throw new RPCException("未找到暴露的服务:{}", interfaceMame);
        }
        return (ServiceConfig<T>) exporters.get(interfaceMame).getServiceConfig();
    }
}
