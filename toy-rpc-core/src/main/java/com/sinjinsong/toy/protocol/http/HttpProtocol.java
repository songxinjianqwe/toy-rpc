package com.sinjinsong.toy.protocol.http;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractProtocol;

/**
 * @author sinjinsong
 * @date 2018/7/18
 */
public class HttpProtocol extends AbstractProtocol {
    
    @Override
    public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) throws RPCException {
        return null;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type) throws RPCException {
        return null;
    }
}
