package com.sinjinsong.rpc.core.client.call.async;

import com.sinjinsong.rpc.core.annotation.RPCReference;
import com.sinjinsong.rpc.core.client.RPCClient;
import com.sinjinsong.rpc.core.client.call.CallHandler;
import com.sinjinsong.rpc.core.client.context.RPCThreadLocalContext;
import com.sinjinsong.rpc.core.domain.RPCRequest;
import com.sinjinsong.rpc.core.domain.RPCResponse;

import java.util.concurrent.Future;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public class AsyncCallHandler extends CallHandler {

    public AsyncCallHandler(RPCClient rpcClient) {
        super(rpcClient);
    }
    
    @Override
    public Object handleCall(RPCRequest request, RPCReference rpcReference) throws Throwable {
        Future<RPCResponse> future = rpcClient.execute(request);
        RPCThreadLocalContext.getContext().setFuture(future);
        return null;
    }
}
