package com.sinjinsong.toy.protocol.injvm;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractProtocol;
import com.sinjinsong.toy.transport.api.Endpoint;

/**
 * @author sinjinsong
 * @date 2018/7/18
 */
public class InJvmProtocol extends AbstractProtocol {

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException {
        return null;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type) throws RPCException {
        return null;
    }

    @Override
    public Endpoint openClient(String address, ApplicationConfig applicationConfig) {
        return null;
    }

    @Override
    public void openServer(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registryConfig, ProtocolConfig protocolConfig) {

    }
}
