package cn.sinjinsong.rpc.core.client;

import cn.sinjinsong.rpc.core.domain.Message;
import cn.sinjinsong.rpc.core.domain.RPCResponse;
import cn.sinjinsong.rpc.core.domain.RPCResponseFuture;
import cn.sinjinsong.rpc.core.enumeration.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by SinjinSong on 2017/7/31.
 */
@Slf4j
public class RPCClientHandler extends SimpleChannelInboundHandler<Message> {
    private Map<String, RPCResponseFuture> responses;


    public RPCClientHandler(Map<String, RPCResponseFuture> responses) {
        this.responses = responses;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message message) throws Exception {
        log.info("接收到服务器响应: {}", message);
        //服务器不会PING客户端
        if (message.getType() == MessageType.PONG) {
            log.info("收到服务器的PONG心跳响应");
        } else if (message.getType() == MessageType.NORMAL) {
            log.info("{}",message.getClass().getName());
            RPCResponse response = (RPCResponse) message;
            if (responses.containsKey(response.getRequestId())) {
                RPCResponseFuture future = responses.remove(response.getRequestId());
                future.setResponse(response);
            }
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

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端通道已开启...");
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
            IdleStateEvent event = (IdleStateEvent) evt;
            log.info("超过指定时间未发送数据，发送心跳信息");
            ctx.writeAndFlush(Message.PING);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
