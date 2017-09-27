package cn.sinjinsong.rpc.server;

import cn.sinjinsong.rpc.domain.common.Message;
import cn.sinjinsong.rpc.domain.common.RPCRequest;
import cn.sinjinsong.rpc.enumeration.MessageType;
import cn.sinjinsong.rpc.util.AnnotationUtil;
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
 * 实际的业务处理器
 */
@Slf4j
public class RPCServerHandler extends SimpleChannelInboundHandler<Message> {
    private Map<String, Object> handlerMap;
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100), new ThreadPoolExecutor.CallerRunsPolicy());

    public RPCServerHandler() {
        handlerMap = AnnotationUtil.getServices();
        log.info("{}",handlerMap);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            cause.printStackTrace();
        } finally {
            ctx.close();
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message message) throws Exception {
        log.info("服务器已接收请求 {}，请求类型 {}", message, message.getType());

        if (message.getType() == MessageType.PING) {
            log.info("收到客户端PING心跳请求，发送PONG心跳响应");
            ctx.writeAndFlush(Message.PONG);
        } else if (message.getType() == MessageType.NORMAL) {
            pool.submit(new RequestExecutor(ctx, (RPCRequest) message, handlerMap));
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
            ctx.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
