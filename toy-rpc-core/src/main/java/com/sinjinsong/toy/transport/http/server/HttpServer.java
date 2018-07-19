package com.sinjinsong.toy.transport.http.server;

import com.sinjinsong.toy.transport.api.MessageConverter;
import com.sinjinsong.toy.transport.api.support.netty.AbstractNettyServer;
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
public class HttpServer extends AbstractNettyServer {
    
    @Override
    protected ChannelInitializer initPipeline() {
        return new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                //编码是其他格式转为字节
                //解码是从字节转到其他格式
                //服务器是把先请求转为POJO（解码），再把响应转为字节（编码）
                //而客户端是先把请求转为字节（编码)，再把响应转为POJO（解码）
                // 在InboundHandler执行完成需要调用Outbound的时候，比如ChannelHandlerContext.write()方法，
                // Netty是直接从该InboundHandler返回逆序的查找该InboundHandler之前的OutboundHandler，并非从Pipeline的最后一项Handler开始查找
                ch.pipeline()
                        .addLast("HttpRequestDecoder", new HttpRequestDecoder())
                        .addLast("HttpResponseEncoder", new HttpResponseEncoder())
                        .addLast("HttpObjectAggregator", new HttpObjectAggregator(10*1024*1024))
                        .addLast("HttpServerHandler", new HttpServerHandler(HttpServer.this,HttpMessageConverter.getInstance(getApplicationConfig().getSerializerInstance())));
            }
        };
    }

    @Override
    protected MessageConverter initConverter() {
        return HttpMessageConverter.getInstance(getApplicationConfig().getSerializerInstance());
    }
}
