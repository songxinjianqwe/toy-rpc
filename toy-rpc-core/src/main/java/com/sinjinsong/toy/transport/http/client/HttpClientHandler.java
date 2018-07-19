package com.sinjinsong.toy.transport.http.client;

import com.sinjinsong.toy.transport.api.Endpoint;
import com.sinjinsong.toy.transport.api.MessageConverter;
import com.sinjinsong.toy.transport.api.domain.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
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
    private Endpoint endpoint;
    private MessageConverter converter;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端与服务器{}通道已开启...", endpoint.getAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            return;
        }
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        Message message = converter.convert2Message(httpRequest);
        log.info("接收到服务器 {} 响应: {}", endpoint.getAddress(), message.getResponse());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        endpoint.handleException(cause);
    }
}
