package com.sinjinsong.toy.transport.api;

import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Future;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public interface Endpoint {
    Future<RPCResponse> submit(RPCRequest request);

    void close();

    ServiceURL getServiceURL();

    void handleException(Throwable throwable);
    
    void handleCallbackRequest(RPCRequest request, ChannelHandlerContext ctx);
    
    void handleRPCResponse(RPCResponse response);
    
    
}
