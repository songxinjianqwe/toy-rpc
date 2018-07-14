package com.sinjinsong.toy.config;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public class AbstractConfig {
    protected ApplicationConfig applicationConfig;
    protected ClusterConfig clusterConfig;
    protected ProtocolConfig protocolConfig;
    protected RegistryConfig registryConfig;
    
    public void init(ApplicationConfig applicationConfig,
                     ClusterConfig clusterConfig,
                     ProtocolConfig protocolConfig,
                     RegistryConfig registryConfig) {
        this.applicationConfig = applicationConfig;
        this.clusterConfig = clusterConfig;
        this.protocolConfig = protocolConfig;
        this.registryConfig = registryConfig;
    }

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public ClusterConfig getClusterConfig() {
        return clusterConfig;
    }

    public ProtocolConfig getProtocolConfig() {
        return protocolConfig;
    }

    public RegistryConfig getRegistryConfig() {
        return registryConfig;
    }
}
