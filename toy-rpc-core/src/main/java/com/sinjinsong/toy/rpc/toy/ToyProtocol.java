package com.sinjinsong.toy.rpc.toy;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.rpc.api.Exporter;
import com.sinjinsong.toy.rpc.api.Invoker;
import com.sinjinsong.toy.rpc.api.Protocol;

import java.net.URL;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public class ToyProtocol implements Protocol {
    
    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RPCException {
        return null;
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RPCException {
        return null;
    }
}
