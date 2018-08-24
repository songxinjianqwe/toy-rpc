package com.sinjinsong.toy.protocol.toy;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractRemoteProtocol;
import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.Client;
import com.sinjinsong.toy.transport.api.Server;
import com.sinjinsong.toy.transport.toy.client.ToyClient;
import com.sinjinsong.toy.transport.toy.server.ToyServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
@Slf4j
public class ToyProtocol extends AbstractRemoteProtocol {

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException {
        ToyExporter<T> exporter = new ToyExporter<>();
        exporter.setInvoker(invoker);
        exporter.setServiceConfig(serviceConfig);
        putExporter(invoker.getInterface(), exporter);
        openServer();
        // export
        try {
            serviceConfig.getRegistryConfig().getRegistryInstance().register(InetAddress.getLocalHost().getHostAddress() + ":" + getGlobalConfig().getPort(), serviceConfig.getInterfaceName());
        } catch (UnknownHostException e) {
            throw new RPCException(e, ErrorEnum.READ_LOCALHOST_ERROR, "获取本地Host失败");
        }
        return exporter;
    }

    @Override
    public <T> Invoker<T> refer(ReferenceConfig<T> referenceConfig, ServiceURL serviceURL) throws RPCException {
        ToyInvoker<T> invoker = new ToyInvoker<>();
        invoker.setInterfaceClass(referenceConfig.getInterfaceClass());
        invoker.setInterfaceName(referenceConfig.getInterfaceName());
        invoker.setGlobalConfig(getGlobalConfig());
        invoker.setClient(initClient(serviceURL));
        List<Filter> filters = referenceConfig.getFilters();
        if (filters.size() == 0) {
            return invoker;
        } else {
            return invoker.buildFilterChain(filters);
        }
    }

    @Override
    protected Client doInitClient(ServiceURL serviceURL) {
        ToyClient toyClient = new ToyClient();
        toyClient.init(getGlobalConfig(), serviceURL);
        return toyClient;
    }

    @Override
    protected Server doOpenServer() {
        ToyServer toyServer = new ToyServer();
        toyServer.init(getGlobalConfig());
        toyServer.run();
        return toyServer;
    }
}
