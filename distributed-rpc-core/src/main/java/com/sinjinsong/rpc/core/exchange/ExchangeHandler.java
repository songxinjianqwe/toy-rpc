package com.sinjinsong.rpc.core.exchange;

import com.sinjinsong.rpc.core.config.ReferenceConfig;
import com.sinjinsong.rpc.core.transport.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface ExchangeHandler {
    Object handleExchange(RPCRequest request, ReferenceConfig referenceConfig) throws Throwable;
}
