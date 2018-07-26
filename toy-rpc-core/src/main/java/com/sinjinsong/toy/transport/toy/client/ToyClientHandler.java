package com.sinjinsong.toy.transport.toy.client;

import com.sinjinsong.toy.transport.api.Client;
import com.sinjinsong.toy.transport.api.domain.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by SinjinSong on 2017/7/31.
 */
@Slf4j
@ChannelHandler.Sharable
@AllArgsConstructor
public class ToyClientHandler extends SimpleChannelInboundHandler<Message> {
    private Client client;
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("客户端捕获到异常");
        cause.printStackTrace();
        log.info("与服务器{} 的连接断开", client.getServiceURL().getAddress());
        client.handleException(cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端与服务器{}通道已开启...", client.getServiceURL().getAddress());
    }

    /**
     * 当超过规定时间，客户端未读写数据，那么会自动调用userEventTriggered方法，向服务器发送一个心跳包
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.info("超过指定时间未发送数据，客户端主动发送心跳信息至{}", client.getServiceURL().getAddress());
            ctx.writeAndFlush(Message.PING_MSG);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        log.info("接收到服务器 {} 响应: {}", client.getServiceURL().getAddress(), message);
        //服务器不会PING客户端
        if (message.getType() == Message.PONG) {
            log.info("收到服务器的PONG心跳响应");
        } else if (message.getType() == Message.RESPONSE) {
            client.handleRPCResponse(message.getResponse());
        } else if (message.getType() == Message.REQUEST) {
            client.handleCallbackRequest(message.getRequest(), ctx);
        }
    }
}
