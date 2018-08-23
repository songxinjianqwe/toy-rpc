package com.sinjinsong.toy.transport.toy.server;


import com.sinjinsong.toy.transport.api.Server;
import com.sinjinsong.toy.common.domain.Message;
import com.sinjinsong.toy.transport.toy.constant.ToyConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

import static com.sinjinsong.toy.common.domain.Message.PING;
import static com.sinjinsong.toy.common.domain.Message.REQUEST;

/**
 * Created by SinjinSong on 2017/7/29.
 * 实际的业务处理器
 * 每个客户端channel都对应一个handler，所以这里的timeoutCount不需要设置成Map
 */
@Slf4j
public class ToyServerHandler extends SimpleChannelInboundHandler<Message> {
    private Server server;
    private AtomicInteger timeoutCount = new AtomicInteger(0);

    public ToyServerHandler(Server server) {
        this.server = server;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        log.info("服务器已接收请求 {}，请求类型 {}", message, message.getType());
        timeoutCount.set(0);
        if (message.getType() == PING) {
            log.info("收到客户端PING心跳请求,对其响应PONG心跳");
             ctx.writeAndFlush(Message.PONG_MSG);
        } else if (message.getType() == REQUEST) {
            server.handleRPCRequest(message.getRequest(), ctx);
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
     * // TODO check 是否有线程安全问题
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (timeoutCount.getAndIncrement() >= ToyConstant.HEART_BEAT_TIME_OUT_MAX_TIME) {
                ctx.close();
                log.info("超过丢失心跳的次数阈值，关闭连接");
            }else {
                log.info("超过规定时间服务器未收到客户端的心跳或正常信息");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
