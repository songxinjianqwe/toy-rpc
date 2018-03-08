package com.sinjinsong.rpc.core.server;

import com.sinjinsong.rpc.core.domain.Message;
import com.sinjinsong.rpc.core.domain.RPCRequest;
import com.sinjinsong.rpc.core.enumeration.MessageType;
import com.sinjinsong.rpc.core.util.AnnotationUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by SinjinSong on 2017/7/29.
 * 实际的业务处理器，单例
 */
@Slf4j
public class RPCServerHandler extends SimpleChannelInboundHandler<Message> {
    private Map<String, Object> handlerMap;
    private ThreadPoolExecutor pool;
    
    public RPCServerHandler() {
        this.handlerMap = AnnotationUtil.getServices();
        int threads = Runtime.getRuntime().availableProcessors();
        this.pool = new ThreadPoolExecutor(threads, threads, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
        log.info("{}",handlerMap);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        log.info("服务器已接收请求 {}，请求类型 {}", message, message.getType());

        if (message.getType() == MessageType.PING) {
            log.info("收到客户端PING心跳请求，发送PONG心跳响应");
            ctx.writeAndFlush(Message.PONG);
        } else if (message.getType() == MessageType.NORMAL) {
            pool.submit(new RequestExecutor(ctx, (RPCRequest) message, handlerMap));
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
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
