package com.sinjinsong.toy.transport.toy.client;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.transport.api.Client;
import com.sinjinsong.toy.transport.api.domain.Message;
import com.sinjinsong.toy.transport.toy.constant.ToyConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by SinjinSong on 2017/7/31.
 */
@Slf4j
public class ToyClientHandler extends SimpleChannelInboundHandler<Message> {
    private Client client;
    private AtomicInteger timeoutCount = new AtomicInteger(0);

    public ToyClientHandler(Client client) {
        this.client = client;
    }

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
     * 心跳是用来检测连接断开/应用宕机以及应用仍然在运行，但无法继续提供服务的情况。
     * 1、一端机器宕机后另一端未必会就收到连接断开事件，只有在发送数据的时候才会，这就需要心跳来检测机器是否宕机（连接是否断开）。
     * 2、假如连接没有断开，那么心跳的作用是检测客户端或服务器繁忙或死锁。
     * 假如说客户端繁忙或死锁，那么服务器会在180s内都没有接收到客户端的数据或PING心跳，为了节省资源，会关掉不必要的连接。当客户端重新请求建立连接时，会重新建立连接。
     * 假如说服务器繁忙或死锁，那么客户端会在180s内都没有接收到服务端的PONG心跳，为了避免对繁忙的服务器再造成更多压力，则考虑对请求直接拒绝掉，不发送给服务器;并且开始重连。
     * 
     * 3、如果检测到了连接断开，那么客户端会进行重连，每隔60s进行重连，只要ZK存在该服务器地址，则进行无限重连。在重连期间，对请求直接拒绝掉。
     * 
     * 
     * 客户端在60s内如果没有写数据，则发送一次心跳。
     * 服务器在60s内如果没有读到数据或心跳，则计数器+1，当计数器达到阈值，则视为客户端已经宕机，关闭连接；当服务器收到PING心跳时，发送PONG心跳，重置计数器（收到数据也重置）
     * 客户端当发现断开连接时，进行重连。
     * 客户端同样执行这种操作，如果三次心跳未收到PONG，则不再发送请求。
     * 
     * 
     * TCP的心跳是2小时触发一次，它的缺点一个是时间非常固定，无法自行调整；另一个是服务器在死锁等情况下，应用程序是无法响应心跳的，但是是可以在OS层进行TCP的心跳响应的，
     * 但是此时不应该发出响应的，所以会给客户端这样的误解：服务器仍然是可以提供服务的。
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (timeoutCount.getAndIncrement() >= ToyConstant.HEART_BEAT_TIME_OUT_MAX_TIME) {
                // 尝试重连,当前handler生命周期已经结束
                client.handleException(new RPCException(ErrorEnum.HEART_BEAT_TIME_OUT_EXCEED,"{} 超过心跳重试次数",ctx.channel()));
            } else {
                log.info("超过指定时间未发送数据，客户端主动发送心跳信息至{}", client.getServiceURL().getAddress());
                ctx.writeAndFlush(Message.PING_MSG);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        log.info("接收到服务器 {} 响应: {}", client.getServiceURL().getAddress(), message);
        timeoutCount.set(0);
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
