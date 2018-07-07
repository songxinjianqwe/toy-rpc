package com.sinjinsong.toy.remoting;

import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.remoting.transport.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface ExchangeHandler {
    Object handleExchange(RPCRequest request, ReferenceConfig referenceConfig) throws Throwable;
}
