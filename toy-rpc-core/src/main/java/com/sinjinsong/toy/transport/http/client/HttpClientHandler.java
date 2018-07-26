package com.sinjinsong.toy.transport.http.client;

import com.sinjinsong.toy.transport.api.Client;
import com.sinjinsong.toy.transport.api.converter.ClientMessageConverter;
import com.sinjinsong.toy.transport.api.domain.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
@Slf4j
@AllArgsConstructor
@ChannelHandler.Sharable
public class HttpClientHandler extends ChannelInboundHandlerAdapter {
    private Client client;
    private ClientMessageConverter converter;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端与服务器{}通道已开启...", client.getServiceURL().getAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = converter.convertResponse2Message(msg);
        log.info("接收到服务器 {} 响应: {}", client.getServiceURL().getAddress(), message.getResponse());
        if (message.getType() == Message.RESPONSE) {
            client.handleRPCResponse(message.getResponse());
        } else if (message.getType() == Message.REQUEST) {
            client.handleCallbackRequest(message.getRequest(), ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        client.handleException(cause);
    }
}
