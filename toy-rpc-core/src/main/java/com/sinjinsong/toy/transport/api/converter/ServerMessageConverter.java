package com.sinjinsong.toy.transport.api.converter;

import com.sinjinsong.toy.common.domain.Message;

/**
 * @author sinjinsong
 * @date 2018/7/21
 */
public abstract class ServerMessageConverter implements MessageConverter {
    public abstract Object convertMessage2Response(Message message);
    public abstract Message convertRequest2Message(Object response);

    @Override
    public Object convert2Object(Message message) {
        return convertMessage2Response(message);
    }

    @Override
    public Message convert2Message(Object obj) {
        return convertRequest2Message(obj);
    }

    public static ServerMessageConverter DEFAULT_IMPL = new ServerMessageConverter() {
        @Override
        public Object convertMessage2Response(Message message) {
            return message;
        }

        @Override
        public Message convertRequest2Message(Object response) {
            return (Message) response;
        }
    };
}
