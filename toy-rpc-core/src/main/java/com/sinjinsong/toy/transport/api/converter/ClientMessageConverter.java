package com.sinjinsong.toy.transport.api.converter;

import com.sinjinsong.toy.common.domain.Message;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public abstract class ClientMessageConverter implements MessageConverter{
    public abstract Object convertMessage2Request(Message message);
    public abstract Message convertResponse2Message(Object response);

    @Override
    public Object convert2Object(Message message) {
        return convertMessage2Request(message);
    }

    @Override
    public Message convert2Message(Object obj) {
        return convertResponse2Message(obj);
    }
    
    public static ClientMessageConverter DEFAULT_IMPL = new ClientMessageConverter() {
        @Override
        public Object convertMessage2Request(Message message) {
            return message;
        }

        @Override
        public Message convertResponse2Message(Object response) {
            return (Message) response;
        }
    };
}
