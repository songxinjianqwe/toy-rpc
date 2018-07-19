package com.sinjinsong.toy.transport.api.support;

import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.config.ClusterConfig;
import com.sinjinsong.toy.config.ProtocolConfig;
import com.sinjinsong.toy.config.RegistryConfig;
import com.sinjinsong.toy.transport.api.Server;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public abstract class AbstractServer implements Server {
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    private ApplicationConfig applicationConfig;
    private ClusterConfig clusterConfig;

    public void init(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registry,
                        ProtocolConfig protocolConfig) {
        this.applicationConfig = applicationConfig;
        this.clusterConfig = clusterConfig;
        this.registryConfig = registry;
        this.protocolConfig = protocolConfig;
        doInit();
    }

    protected abstract void doInit();

    @Override
    public RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

    @Override
    public ProtocolConfig getProtocolConfig() {
        return protocolConfig;
    }

    @Override
    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    @Override
    public ClusterConfig getClusterConfig() {
        return clusterConfig;
    }
}
