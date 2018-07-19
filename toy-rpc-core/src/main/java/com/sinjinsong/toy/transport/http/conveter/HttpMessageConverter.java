package com.sinjinsong.toy.transport.http.conveter;

import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.transport.api.MessageConverter;
import com.sinjinsong.toy.transport.api.domain.Message;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public class HttpMessageConverter implements MessageConverter {
    private static HttpMessageConverter ourInstance = new HttpMessageConverter();
    private Serializer serializer;

    public static HttpMessageConverter getInstance(Serializer serializer) {
        ourInstance.serializer = serializer;
        return ourInstance;
    }

    /**
     * 如果message是request，则转为http request
     *
     * @param message
     * @return
     */
    @Override
    public Object convert2Object(Message message, String address) {
        if (message.getType() == Message.REQUEST) {
            DefaultFullHttpRequest request;
            try {
                String uri = "http://" + address + "/" + message.getRequest().getInterfaceName();
                request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, HttpMethod.POST, uri);
                request.headers().set(HttpHeaders.Names.HOST, InetAddress.getLocalHost().getHostAddress());
                request.content().writeBytes(serializer.serialize(message));
                return request;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else if (message.getType() == Message.RESPONSE) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK, Unpooled.wrappedBuffer(serializer.serialize(message)));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH,
                    response.content().readableBytes());
            return response;
        }
        return null;
    }

    /**
     * @param obj
     * @return
     */
    @Override
    public Message convert2Message(Object obj) {
        if (obj instanceof FullHttpRequest) {
            FullHttpRequest content = (FullHttpRequest) obj;
            return serializer.deserialize(content.content().array(), Message.class);
        }
        return null;
    }
}
