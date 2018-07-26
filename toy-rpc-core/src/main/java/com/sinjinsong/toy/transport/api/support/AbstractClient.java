package com.sinjinsong.toy.transport.api.support;

import com.sinjinsong.toy.config.GlobalConfig;
import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.Client;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public abstract class AbstractClient implements Client {
    private ServiceURL serviceURL;
    private GlobalConfig globalConfig;

    public void init(GlobalConfig globalConfig, ServiceURL serviceURL) {
        this.serviceURL = serviceURL;
        this.globalConfig = globalConfig;
        // 初始化的时候建立连接，才能检测到服务器是否可用
        connect();
    }
    
    protected abstract void connect();

    protected GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public ServiceURL getServiceURL() {
        return serviceURL;
    }

    @Override
    public void updateServiceConfig(ServiceURL serviceURL) {
        this.serviceURL = serviceURL;
    }
}
