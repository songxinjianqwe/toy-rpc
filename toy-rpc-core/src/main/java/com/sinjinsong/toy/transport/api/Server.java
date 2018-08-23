package com.sinjinsong.toy.transport.api;

import com.sinjinsong.toy.common.domain.RPCRequest;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public interface Server {
    void run();

    void handleRPCRequest(RPCRequest request, ChannelHandlerContext ctx);

    void close();
}
