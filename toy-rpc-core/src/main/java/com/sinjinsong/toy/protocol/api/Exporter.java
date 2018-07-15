package com.sinjinsong.toy.protocol.api;

import com.sinjinsong.toy.config.ServiceConfig;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Exporter<T> {
     Invoker<T> getInvoker();
     ServiceConfig<T> getServiceConfig();
    /**
     * unexport.
     * <p>
     * <code>
     * getInvoker().destroy();
     * </code>
     */
    void unexport();
}
