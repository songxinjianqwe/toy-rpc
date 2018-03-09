package com.sinjinsong.rpc.core.coder;

import com.sinjinsong.rpc.core.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by SinjinSong on 2017/7/30.
 */
public class RPCEncoder extends MessageToByteEncoder {
    /**
     * 抽象对象。比如Message
     */
    private Class<?> genericClass;
    /**
     * 具体对象，比如RPCRequest/RPCResponse
     */
    private Class<?> concreteClass;

    public RPCEncoder(Class<?> genericClass, Class<?> concreteClass) {
        this.genericClass = genericClass;
        this.concreteClass = concreteClass;
    }
    
    /**
     * 将Object转为ByteBuf
     * 先写一个长度，再写数据
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (concreteClass.isInstance(in) || genericClass.isInstance(in)) {
            byte[] data = ProtostuffUtil.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
