package com.sinjinsong.toy.remoting.exchange.async;

import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.remoting.exchange.support.AbstractExchangeHandler;
import com.sinjinsong.toy.remoting.transport.client.RPCClient;
import com.sinjinsong.toy.rpc.api.RPCThreadLocalContext;
import com.sinjinsong.toy.remoting.transport.domain.RPCRequest;
import com.sinjinsong.toy.remoting.transport.domain.RPCResponse;

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
