package com.sinjinsong.rpc.core.exchange;

import com.sinjinsong.rpc.core.config.ReferenceConfig;
import com.sinjinsong.rpc.core.transport.client.RPCClient;
import com.sinjinsong.rpc.core.transport.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public abstract class AbstractExchangeHandler implements ExchangeHandler{
    protected RPCClient rpcClient;

    public AbstractExchangeHandler(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public abstract Object handleExchange(RPCRequest request, ReferenceConfig referenceConfig) throws Throwable;
}
