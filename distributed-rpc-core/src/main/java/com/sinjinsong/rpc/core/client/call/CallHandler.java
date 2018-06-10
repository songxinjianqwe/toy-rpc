package com.sinjinsong.rpc.core.client.call;

import com.sinjinsong.rpc.core.annotation.RPCReference;
import com.sinjinsong.rpc.core.client.RPCClient;
import com.sinjinsong.rpc.core.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public abstract class CallHandler {
    protected RPCClient rpcClient;

    public CallHandler(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public abstract Object handleCall(RPCRequest request,RPCReference rpcReference) throws Throwable;
}
