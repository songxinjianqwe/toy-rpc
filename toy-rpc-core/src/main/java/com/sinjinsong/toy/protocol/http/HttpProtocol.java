package com.sinjinsong.toy.protocol.http;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractRemoteProtocol;
import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.Endpoint;
import com.sinjinsong.toy.transport.api.Server;
import com.sinjinsong.toy.transport.http.client.HttpEndpoint;
import com.sinjinsong.toy.transport.http.server.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author sinjinsong
 * @date 2018/7/18
 */
@Slf4j
public class HttpProtocol extends AbstractRemoteProtocol {

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException {
        HttpExporter<T> exporter = new HttpExporter<>();
        exporter.setInvoker(invoker);
        exporter.setServiceConfig(serviceConfig);
        putExporter(invoker.getInterface(), exporter);
        // 启动服务器
        // 这个必须在注册到注册中心之前执行
        openServer();
        // export
        try {
            serviceConfig.getRegistryConfig().getRegistryInstance().register(InetAddress.getLocalHost().getHostAddress() + ":" + getProtocolConfig().getPort(), serviceConfig.getInterfaceName());
        } catch (UnknownHostException e) {
            throw new RPCException(e, ErrorEnum.READ_LOCALHOST_ERROR, "获取本地Host失败");
        }
        return exporter;
    }

    @Override
    public <T> Invoker<T> refer(ReferenceConfig<T> referenceConfig, ServiceURL serviceURL) throws RPCException {
        HttpInvoker<T> invoker = new HttpInvoker<>();
        invoker.setInterfaceClass(referenceConfig.getInterfaceClass());
        invoker.setInterfaceName(referenceConfig.getInterfaceName());
        invoker.setEndpoint(initEndpoint(serviceURL));
        invoker.setProtocolConfig(getProtocolConfig());
        return invoker.buildFilterChain(referenceConfig.getFilters());
    }

    @Override
    protected Endpoint doInitEndpoint(ServiceURL serviceURL) {
        HttpEndpoint httpEndpoint = new HttpEndpoint();
        httpEndpoint.init(getApplicationConfig(), serviceURL);
        return httpEndpoint;
    }


    @Override
    protected Server doOpenServer() {
        HttpServer httpServer = new HttpServer();
        httpServer.init(getApplicationConfig(), getClusterConfig(), getRegistryConfig(), getProtocolConfig());
        httpServer.run();
        return httpServer;
    }
}
