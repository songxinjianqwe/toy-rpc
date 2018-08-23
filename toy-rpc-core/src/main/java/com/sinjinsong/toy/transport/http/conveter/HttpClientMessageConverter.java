package com.sinjinsong.toy.transport.http.conveter;

import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.transport.api.converter.ClientMessageConverter;
import com.sinjinsong.toy.common.domain.Message;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
@Slf4j
public class HttpClientMessageConverter extends ClientMessageConverter {
    private static HttpClientMessageConverter ourInstance = new HttpClientMessageConverter();
    private Serializer serializer;

    public static HttpClientMessageConverter getInstance(Serializer serializer) {
        ourInstance.serializer = serializer;
        return ourInstance;
    }

    /**
     * 对于服务器，返回的一定是响应；
     * 对于客户端，返回的一定是请求；
     * 而不是根据message是request 或者 response
     *
     * 
     * 把消息转为request
     * @param message
     * @return
     */
    @Override
    public Object convertMessage2Request(Message message) {
        byte[] body = serializer.serialize(message);
        DefaultFullHttpRequest request;
        try {
            String uri = "http://" + message.getRequest().getInterfaceName();
            request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.POST, uri, Unpooled.wrappedBuffer(body));
            request.headers().set(HttpHeaders.Names.HOST, InetAddress.getLocalHost().getHostAddress());
            request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
            return request;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把response转为消息
     * @param response
     * @return
     */
    @Override
    public Message convertResponse2Message(Object response) {
        if (response instanceof FullHttpResponse) {
            FullHttpResponse fullHttpResponse = (FullHttpResponse) response;
            byte[] body = new byte[fullHttpResponse.content().readableBytes()];
            fullHttpResponse.content().getBytes(0, body);
            log.info("response:{}", fullHttpResponse);
            return serializer.deserialize(body, Message.class);
        }
        return null;
    }
}
