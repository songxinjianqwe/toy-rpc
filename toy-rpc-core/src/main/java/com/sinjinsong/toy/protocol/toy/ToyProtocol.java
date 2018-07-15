package com.sinjinsong.toy.protocol.toy;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractProtocol;
import com.sinjinsong.toy.transport.server.RPCServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public class ToyProtocol extends AbstractProtocol {
    
    private volatile RPCServer rpcServer;
    
    @Override
    public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException {
        ToyExporter<T> exporter = new ToyExporter<>();
        exporter.setInvoker(invoker);
        exporter.setServiceConfig(serviceConfig);
        putExporter(invoker.getInterface(), exporter);
        // export
        try {
            int port = serviceConfig.getProtocolConfig().getPort() != null ? serviceConfig.getProtocolConfig().getPort() : serviceConfig.getProtocolConfig().DEFAULT_PORT ;
            serviceConfig.getRegistryConfig().getRegistryInstance().register(InetAddress.getLocalHost().getHostAddress() + ":" + port, serviceConfig.getInterfaceName());
        } catch (UnknownHostException e) {
            throw new RPCException("获取本地Host失败", e);
        }
        return exporter;
    }
    

    @Override
    public <T> Invoker<T> refer(Class<T> type, ReferenceConfig<T> referenceConfig) throws RPCException {
        ToyInvoker<T> invoker = new ToyInvoker<>();
        invoker.setReferenceConfig(referenceConfig);
        invoker.setInterfaceClass(type);
        // TODO 注入Filters
        invoker.setFilters(new ArrayList<>());
        putInvoker(type, invoker);
        return invoker;
    }
}
