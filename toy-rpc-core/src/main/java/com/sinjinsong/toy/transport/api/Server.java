package com.sinjinsong.toy.transport.api;

import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public interface Server {
    void run();
    void handleRequest(RPCRequest request,ChannelHandlerContext ctx);
}
