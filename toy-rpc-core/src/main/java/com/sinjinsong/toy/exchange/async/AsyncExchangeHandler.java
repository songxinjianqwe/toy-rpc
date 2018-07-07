package com.sinjinsong.toy.exchange.async;

import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.exchange.AbstractExchangeHandler;
import com.sinjinsong.toy.transport.client.RPCClient;
import com.sinjinsong.toy.transport.client.context.RPCThreadLocalContext;
import com.sinjinsong.toy.transport.domain.RPCRequest;
import com.sinjinsong.toy.transport.domain.RPCResponse;

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
