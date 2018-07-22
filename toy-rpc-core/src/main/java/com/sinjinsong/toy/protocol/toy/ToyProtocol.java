package com.sinjinsong.toy.protocol.toy;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractProtocol;
import com.sinjinsong.toy.transport.toy.server.ToyServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
@Slf4j
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
            throw new RPCException(e,"获取本地Host失败");
        }
        return exporter;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type) throws RPCException {
        ToyInvoker<T> invoker = new ToyInvoker<>();
        invoker.setInterfaceClass(type);
        return invoker;
    }


    @Override
    public void doOnApplicationLoadComplete(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registry, ProtocolConfig protocolConfig) {
        log.info("http protocol doOnApplicationLoadComplete...");
        if (isExporterExists()) {
            ToyServer toyServer = new ToyServer();
            toyServer.init(applicationConfig, clusterConfig, registry, protocolConfig);
            toyServer.run();
        }
    }
}
