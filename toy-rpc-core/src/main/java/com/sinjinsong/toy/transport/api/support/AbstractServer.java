package com.sinjinsong.toy.transport.api.support;

import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.transport.api.Server;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public abstract class AbstractServer implements Server {
    private GlobalConfig globalConfig;

    public void init(GlobalConfig globalConfig) {
       this.globalConfig = globalConfig;
        doInit();
    }

    protected GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    protected abstract void doInit();
    
}
