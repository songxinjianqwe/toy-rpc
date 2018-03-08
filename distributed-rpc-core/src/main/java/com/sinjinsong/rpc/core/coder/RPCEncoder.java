package com.sinjinsong.rpc.core.coder;

import com.sinjinsong.rpc.core.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by SinjinSong on 2017/7/30.
 */
public class RPCEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    public RPCEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    /**
     * 将Object转为ByteBuf
     * 先写一个长度，再写数据
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = ProtostuffUtil.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
