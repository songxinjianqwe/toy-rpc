package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.transport.api.Endpoint;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
@Slf4j
public abstract class AbstractRemoteInvoker<T> extends AbstractInvoker<T> {
    private Endpoint endpoint;

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public String getAddress() {
        return getEndpoint().getAddress();
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void close() {
        // 如果是重写了getEndpoint方法而非
        getEndpoint().close();
    }
}
