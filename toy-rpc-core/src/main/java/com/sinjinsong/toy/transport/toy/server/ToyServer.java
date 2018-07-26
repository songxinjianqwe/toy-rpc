package com.sinjinsong.toy.transport.toy.server;

import com.sinjinsong.toy.transport.api.converter.ServerMessageConverter;
import com.sinjinsong.toy.transport.api.constant.FrameConstant;
import com.sinjinsong.toy.transport.api.support.netty.AbstractNettyServer;
import com.sinjinsong.toy.transport.toy.codec.ToyDecoder;
import com.sinjinsong.toy.transport.toy.codec.ToyEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by SinjinSong on 2017/7/29.
 */
@Slf4j
public class ToyServer extends AbstractNettyServer {
    
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
                        .addLast("IdleStateHandler", new IdleStateHandler(10, 0, 0))
                        // ByteBuf -> Message 
                        .addLast("LengthFieldPrepender", new LengthFieldPrepender(FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT))
                        // Message -> ByteBuf
                        .addLast("ToyEncoder", new ToyEncoder(getGlobalConfig().getSerializer()))
                        // ByteBuf -> Message
                        .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(FrameConstant.MAX_FRAME_LENGTH, FrameConstant.LENGTH_FIELD_OFFSET, FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT, FrameConstant.INITIAL_BYTES_TO_STRIP))
                        // Message -> Message
                        .addLast("ToyDecoder", new ToyDecoder(getGlobalConfig().getSerializer()))
                        .addLast("ToyServerHandler", new ToyServerHandler(ToyServer.this));
            }
        };
    }

    @Override
    protected ServerMessageConverter initConverter() {
        return ServerMessageConverter.DEFAULT_IMPL;
    }
}
