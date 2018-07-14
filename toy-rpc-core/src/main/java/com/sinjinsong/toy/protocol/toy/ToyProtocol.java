package com.sinjinsong.toy.protocol.toy;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractProtocol;

import java.util.ArrayList;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public class ToyProtocol extends AbstractProtocol {
        
    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RPCException {
        return null;
    }
    
    @Override
    public <T> Invoker<T> refer(Class<T> type, ReferenceConfig<T> referenceConfig) throws RPCException {
        ToyInvoker<T> invoker = new ToyInvoker<>();
        invoker.setReferenceConfig(referenceConfig);
        invoker.setInterfaceClass(type);
        // TODO 注入Filters
        invoker.setFilters(new ArrayList<>());
        return invoker;
    }
}
