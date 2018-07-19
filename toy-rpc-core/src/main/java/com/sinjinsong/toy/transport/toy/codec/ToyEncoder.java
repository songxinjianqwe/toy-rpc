package com.sinjinsong.toy.transport.toy.codec;

import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.transport.api.domain.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Slf4j
public class ToyEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    public ToyEncoder(Serializer serializer) {
        this.serializer = serializer;
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Message message = (Message) msg;
        out.writeByte((message.getType()));
        if (message.getType() == Message.REQUEST) {
            out.writeBytes(serializer.serialize(message.getRequest()));
        }
        if (message.getType() == Message.RESPONSE) {
            out.writeBytes(serializer.serialize(message.getResponse()));
        }
    }
}
