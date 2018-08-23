package com.sinjinsong.toy.transport.http.server;

import com.sinjinsong.toy.transport.api.Server;
import com.sinjinsong.toy.transport.api.converter.ServerMessageConverter;
import com.sinjinsong.toy.common.domain.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private Server server;
    private ServerMessageConverter converter;
    
    private static HttpServerHandler INSTANCE;

    private HttpServerHandler(Server server, ServerMessageConverter converter) {
        this.server = server;
        this.converter = converter;
    }

    public synchronized static void init(Server server,ServerMessageConverter converter) {
        if (INSTANCE == null) {
            INSTANCE = new HttpServerHandler(server,converter);
        }
    }

    public static HttpServerHandler getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("instance did not initialize");
        }
        return INSTANCE;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelUnregistered:{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive:{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive:{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("channelReadComplete:{}", ctx.channel().remoteAddress());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        Message message = converter.convertRequest2Message(request);
        log.info("服务器已接收请求 {}，请求类型 {}", message, message.getType());
        server.handleRPCRequest(message.getRequest(), ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelRegistered:{}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            cause.printStackTrace();
        } finally {
            ctx.close();
        }
    }
}