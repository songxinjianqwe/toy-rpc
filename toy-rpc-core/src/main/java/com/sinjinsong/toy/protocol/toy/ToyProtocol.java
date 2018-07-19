package com.sinjinsong.toy.protocol.toy;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractProtocol;
import com.sinjinsong.toy.transport.api.Endpoint;
import com.sinjinsong.toy.transport.toy.client.ToyEndpoint;
import com.sinjinsong.toy.transport.toy.server.ToyServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public class ToyProtocol extends AbstractProtocol {

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException {
        ToyExporter<T> exporter = new ToyExporter<>();
        exporter.setInvoker(invoker);
        exporter.setServiceConfig(serviceConfig);
        putExporter(invoker.getInterface(), exporter);
        // export
        try {
            int port = serviceConfig.getProtocolConfig().getPort() != null ? serviceConfig.getProtocolConfig().getPort() : serviceConfig.getProtocolConfig().DEFAULT_PORT;
            serviceConfig.getRegistryConfig().getRegistryInstance().register(InetAddress.getLocalHost().getHostAddress() + ":" + port, serviceConfig.getInterfaceName());
        } catch (UnknownHostException e) {
            throw new RPCException("获取本地Host失败", e);
        }
        return exporter;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type) throws RPCException {
        ToyInvoker<T> invoker = new ToyInvoker<>();
        invoker.setInterfaceClass(type);
        Invoker<T> invokerWithFilters = invoker.buildFilterChain(ReferenceConfig.getFiltersByInterface(type));
        putInvoker(type, invokerWithFilters);
        return invokerWithFilters;
    }

    @Override
    public Endpoint openClient(String address, ApplicationConfig applicationConfig) {
        ToyEndpoint toyEndpoint = new ToyEndpoint();
        toyEndpoint.init(applicationConfig, address);
        return toyEndpoint;
    }

    @Override
    public void openServer(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registry, ProtocolConfig protocolConfig) {
        ToyServer toyServer = new ToyServer();
        toyServer.init(applicationConfig, clusterConfig, registry, protocolConfig);
        toyServer.run();
    }
}
