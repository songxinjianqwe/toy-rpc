package com.sinjinsong.toy.protocol.http;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractProtocol;
import com.sinjinsong.toy.transport.api.Endpoint;
import com.sinjinsong.toy.transport.http.client.HttpEndpoint;
import com.sinjinsong.toy.transport.http.server.HttpServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author sinjinsong
 * @date 2018/7/18
 */
public class HttpProtocol extends AbstractProtocol {

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException {
        HttpExporter<T> exporter = new HttpExporter<>();
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
        HttpInvoker<T> invoker = new HttpInvoker<>();
        invoker.setInterfaceClass(type);
        Invoker<T> invokerWithFilters = invoker.buildFilterChain(ReferenceConfig.getFiltersByInterface(type));
        putInvoker(type, invokerWithFilters);
        return invokerWithFilters;
    }

    @Override
    public Endpoint openClient(String address, ApplicationConfig applicationConfig) {
        HttpEndpoint httpEndpoint = new HttpEndpoint();
        httpEndpoint.init(applicationConfig, address);
        return httpEndpoint;
    }


    @Override
    public void openServer(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registry, ProtocolConfig protocolConfig) {
        HttpServer httpServer = new HttpServer();
        httpServer.init(applicationConfig, clusterConfig, registry, protocolConfig);
        httpServer.run();
    }
}
