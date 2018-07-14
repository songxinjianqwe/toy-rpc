package com.sinjinsong.toy.transport.common.codec;

import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.transport.common.domain.Message;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Slf4j
public class RPCDecoder extends ByteToMessageDecoder {
 private Serializer serializer;
    public RPCDecoder(Serializer serializer) {
        this.serializer = serializer;
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte type = in.readByte();
        if (type == Message.PING) {
            out.add(Message.PING_MSG);
        } else if (type == Message.PONG) {
            out.add(Message.PONG_MSG);
        } else {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            if (type == Message.REQUEST) {
                out.add(Message.buildRequest(serializer.deserialize(bytes, RPCRequest.class)));
            } else if (type == Message.RESPONSE) {
                out.add(Message.buildResponse(serializer.deserialize(bytes, RPCResponse.class)));
            }
        }
    }
}
