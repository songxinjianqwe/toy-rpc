package com.sinjinsong.toy.protocol.injvm;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractProtocol;
import com.sinjinsong.toy.registry.api.ServiceURL;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/18
 */
@Slf4j
public class InJvmProtocol extends AbstractProtocol {

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException {
        InJvmExporter<T> exporter = new InJvmExporter<>();
        exporter.setInvoker(invoker);
        exporter.setServiceConfig(serviceConfig);
        putExporter(invoker.getInterface(), exporter);
        // export
        return exporter;
    }

    @Override
    public <T> Invoker<T> refer(ReferenceConfig<T> referenceConfig,ServiceURL serviceURL) throws RPCException {
        InJvmInvoker<T> invoker = new InJvmInvoker<>();
        invoker.setInterfaceClass(referenceConfig.getInterfaceClass());
        invoker.setInterfaceName(referenceConfig.getInterfaceName());
        invoker.setGlobalConfig(getGlobalConfig());
        return invoker.buildFilterChain(referenceConfig.getFilters());
    }
}
