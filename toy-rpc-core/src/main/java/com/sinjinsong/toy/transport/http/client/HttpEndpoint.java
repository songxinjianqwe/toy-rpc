package com.sinjinsong.toy.transport.http.client;

import com.sinjinsong.toy.transport.api.support.netty.AbstractNettyEndpoint;
import com.sinjinsong.toy.transport.http.conveter.HttpMessageConverter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
@Slf4j
public class HttpEndpoint extends AbstractNettyEndpoint {
    
    @Override
    protected ChannelInitializer initPipeline() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast("HttpResponseEncoder", new HttpResponseEncoder())
                        .addLast("HttpRequestDecoder", new HttpRequestDecoder())
                        .addLast("HttpObjectAggregator", new HttpObjectAggregator(10*1024*1024))
                        .addLast("HttpClientHandler", new HttpClientHandler(HttpEndpoint.this,HttpMessageConverter.getInstance(getApplicationConfig().getSerializerInstance())));
            }
        };
    }

    @Override
    protected HttpMessageConverter initConverter() {
        return HttpMessageConverter.getInstance(getApplicationConfig().getSerializerInstance());
    }
}
