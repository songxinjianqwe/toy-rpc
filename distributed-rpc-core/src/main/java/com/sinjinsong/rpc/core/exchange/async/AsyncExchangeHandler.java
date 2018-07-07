package com.sinjinsong.rpc.core.exchange.async;

import com.sinjinsong.rpc.core.config.ReferenceConfig;
import com.sinjinsong.rpc.core.exchange.AbstractExchangeHandler;
import com.sinjinsong.rpc.core.transport.client.RPCClient;
import com.sinjinsong.rpc.core.transport.client.context.RPCThreadLocalContext;
import com.sinjinsong.rpc.core.transport.domain.RPCRequest;
import com.sinjinsong.rpc.core.transport.domain.RPCResponse;

import java.util.concurrent.Future;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public class AsyncExchangeHandler extends AbstractExchangeHandler {

    public AsyncExchangeHandler(RPCClient rpcClient) {
        super(rpcClient);
    }
    
    @Override
    public Object handleExchange(RPCRequest request, ReferenceConfig referenceConfig) throws Throwable {
        Future<RPCResponse> future = rpcClient.execute(request);
        RPCThreadLocalContext.getContext().setFuture(future);
        return null;
    }
}
