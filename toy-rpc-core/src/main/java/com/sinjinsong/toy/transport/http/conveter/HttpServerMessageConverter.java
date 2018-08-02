package com.sinjinsong.toy.transport.http.conveter;

import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.transport.api.converter.ServerMessageConverter;
import com.sinjinsong.toy.transport.api.domain.Message;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

/**
 * @author sinjinsong
 * @date 2018/7/21
 */
@Slf4j
public class HttpServerMessageConverter extends ServerMessageConverter {
    private static HttpServerMessageConverter ourInstance = new HttpServerMessageConverter();
    private Serializer serializer;

    public static HttpServerMessageConverter getInstance(Serializer serializer) {
        ourInstance.serializer = serializer;
        return ourInstance;
    }

    /**
     * 把消息转为response
     * @param message
     * @return
     */
    @Override
    public Object convertMessage2Response(Message message) {
         byte[] body = serializer.serialize(message);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, Unpooled.wrappedBuffer(body));
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().set(CONTENT_LENGTH,
                response.content().readableBytes());
        return response;
    }

    /**
     * 把request转为message
     * @param response
     * @return
     */
    @Override
    public Message convertRequest2Message(Object response) {
        if (response instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) response;
            // 这里是direct buffer，不能调用ByteBuf的array()
            byte[] body = new byte[request.content().readableBytes()];
            request.content().getBytes(0, body);
            log.info("request:{}", request);
            return serializer.deserialize(body, Message.class);
        }
        return null;
    }
}
