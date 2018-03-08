package com.sinjinsong.rpc.core.coder;

import com.sinjinsong.rpc.core.enumeration.MessageType;
import com.sinjinsong.rpc.core.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Slf4j
public class RPCDecoder extends ByteToMessageDecoder {
    /**
     * 抽象对象。比如Message
     */
    private Class<?> genericClass;
    /**
     * 具体对象，比如RPCRequest/RPCResponse
     */
    private Class<?> concreteClass;

    public RPCDecoder(Class<?> genericClass, Class<?> concreteClass) {
        this.genericClass = genericClass;
        this.concreteClass = concreteClass;
    }


    /**
     * 将ByteBuf转为List<Object>
     * 先读一个长度，再按长度读取Body
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("接收到数据");
        if (in.readableBytes() < 4) {
            log.info("数据包没有包含长度字段，退出");
            ctx.close();
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength <= 0) {
            log.info("数据包的长度字段小于等于0，退出");
            ctx.close();
            return;
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            ctx.close();
            log.info("数据包的长度字段小于可读字节数，与事实不符，退出");
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = ProtostuffUtil.deserialize(data, genericClass);
        Field type = obj.getClass().getDeclaredField("type");
        type.setAccessible(true);
        if (type.get(obj) == MessageType.NORMAL) {
            log.info("按更为具体的类型进行反序列化");
            obj = ProtostuffUtil.deserialize(data,concreteClass);
        }
        log.info("解码结束");
        out.add(obj);
    }
}
