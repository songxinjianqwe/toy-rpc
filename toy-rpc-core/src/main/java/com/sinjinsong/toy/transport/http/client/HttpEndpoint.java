package com.sinjinsong.toy.transport.http.client;

import com.sinjinsong.toy.transport.api.support.netty.AbstractNettyEndpoint;
import com.sinjinsong.toy.transport.http.conveter.HttpClientMessageConverter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
@Slf4j
public class HttpEndpoint extends AbstractNettyEndpoint {

    @Override
    protected ChannelInitializer initPipeline() {
        log.info("HttpEndpoint initPipeline...");
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        // 客户端会发出请求，接收响应；也有可能会接收请求（但消息体也是响应,callback）
                        // 服务器会接收请求，发出响应，也有可能会发出请求（但消息体也是响应,callback）
                        .addLast("HttpRequestEncoder", new HttpRequestEncoder())
                        .addLast("HttpResponseDecoder", new HttpResponseDecoder())
                        .addLast("HttpObjectAggregator",new HttpObjectAggregator(10*1024*1024))
                        .addLast("HttpClientHandler", new HttpClientHandler(HttpEndpoint.this, HttpClientMessageConverter.getInstance(getApplicationConfig().getSerializerInstance())));
            }
        };
    }

    @Override
    protected HttpClientMessageConverter initConverter() {
        return HttpClientMessageConverter.getInstance(getApplicationConfig().getSerializerInstance());
    }
}
