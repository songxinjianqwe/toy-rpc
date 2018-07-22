package com.sinjinsong.toy.protocol.injvm;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractProtocol;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
        try {
            int port = serviceConfig.getProtocolConfig().getPort() != null ? serviceConfig.getProtocolConfig().getPort() : serviceConfig.getProtocolConfig().DEFAULT_PORT;
            serviceConfig.getRegistryConfig().getRegistryInstance().register(InetAddress.getLocalHost().getHostAddress() + ":" + port, serviceConfig.getInterfaceName());
        } catch (UnknownHostException e) {
            throw new RPCException(e,ErrorEnum.READ_LOCALHOST_ERROR,"获取本地Host失败");
        }
        return exporter;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type) throws RPCException {
        InJvmInvoker<T> invoker = new InJvmInvoker<>();
        invoker.setInterfaceClass(type);
        invoker.setProtocol(this);
        // 对于RemoteInvoker来说，这一步是在initEndpoint的时候做的，如果非要要在refer时做，那么这时候
        // 返回的包装后的Invoker只是AbstractInvoker类型，无法根据是否为RemoveInvoker来决定
        // 是否初始化Endpoint
        // 后果就是返回了一个AbstractInvoker，跳过了初始化Endpoint的阶段
        Invoker<T> invokerWithFilters = invoker.buildFilterChain(ReferenceConfig.getReferenceConfigByInterface(type).getFilters());
        return invokerWithFilters;
    }
    
    @Override
    public void doOnApplicationLoadComplete(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registryConfig, ProtocolConfig protocolConfig) {
        log.info("http protocol doOnApplicationLoadComplete...");
    }
}
