package com.sinjinsong.toy.transport.toy.client;

import com.sinjinsong.toy.transport.api.MessageConverter;
import com.sinjinsong.toy.transport.api.constant.FrameConstant;
import com.sinjinsong.toy.transport.api.support.netty.AbstractNettyEndpoint;
import com.sinjinsong.toy.transport.toy.codec.ToyDecoder;
import com.sinjinsong.toy.transport.toy.codec.ToyEncoder;
import com.sinjinsong.toy.transport.toy.converter.ToyMessageConverter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;


/**
 * @author sinjinsong
 * @date 2018/6/10
 * 相当于一个客户端连接，对应一个Channel
 * 每个服务器的每个接口对应一个Endpoint
 */
@Slf4j
public class ToyEndpoint extends AbstractNettyEndpoint {


    @Override
    protected ChannelInitializer initPipeline() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast("IdleStateHandler", new IdleStateHandler(0, 5, 0))
                        // ByteBuf -> Message 
                        .addLast("LengthFieldPrepender", new LengthFieldPrepender(FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT))
                        // Message -> ByteBuf
                        .addLast("ToyEncoder", new ToyEncoder(getApplicationConfig().getSerializerInstance()))
                        // ByteBuf -> Message
                        .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(FrameConstant.MAX_FRAME_LENGTH, FrameConstant.LENGTH_FIELD_OFFSET, FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT, FrameConstant.INITIAL_BYTES_TO_STRIP))
                        // Message -> Message
                        .addLast("ToyDecoder", new ToyDecoder(getApplicationConfig().getSerializerInstance()))

                        .addLast("ToyClientHandler", new ToyClientHandler(ToyEndpoint.this));
            }
        };
    }

    @Override
    protected MessageConverter initConverter() {
        return ToyMessageConverter.getInstance();
    }
}
