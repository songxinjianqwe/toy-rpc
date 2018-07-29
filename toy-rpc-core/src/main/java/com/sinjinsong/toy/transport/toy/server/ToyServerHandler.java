package com.sinjinsong.toy.transport.toy.server;


import com.sinjinsong.toy.transport.api.Server;
import com.sinjinsong.toy.transport.api.domain.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.sinjinsong.toy.transport.api.domain.Message.PING;
import static com.sinjinsong.toy.transport.api.domain.Message.REQUEST;

/**
 * Created by SinjinSong on 2017/7/29.
 * 实际的业务处理器，单例
 */
@AllArgsConstructor
@Slf4j
@ChannelHandler.Sharable
public class ToyServerHandler extends SimpleChannelInboundHandler<Message> {
    private static ToyServerHandler INSTANCE;
    
    public synchronized static void init(Server server) {
        if(INSTANCE == null) {
            INSTANCE = new ToyServerHandler(server);
        }
    } 
    
    public static ToyServerHandler getInstance() {
        if(INSTANCE == null) {
            throw new IllegalStateException("instance did not initialize");
        }
        return INSTANCE;
    }    
    
    private Server server;
    
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("接收到客户端的连接");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        log.info("服务器已接收请求 {}，请求类型 {}", message, message.getType());
        if (message.getType() == PING) {
            log.info("收到客户端PING心跳请求，发送PONG心跳响应");
            ctx.writeAndFlush(Message.PONG_MSG);
        } else if (message.getType() == REQUEST) {
            server.handleRPCRequest(message.getRequest(),ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            cause.printStackTrace();
        } finally {
            ctx.close();
        }
    }

    /**
     * 当超过规定时间，服务器未读取到来自客户端的请求，则关闭连接
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.info("超过规定时间服务器仍未收到客户端的心跳或正常信息，关闭连接");
            ctx.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
