package com.sinjinsong.toy.transport.toy.converter;

import com.sinjinsong.toy.transport.api.MessageConverter;
import com.sinjinsong.toy.transport.api.domain.Message;

/**
 * @author sinjinsong
 * @date 2018/7/20
 */
public class ToyMessageConverter implements MessageConverter {
    private static ToyMessageConverter ourInstance = new ToyMessageConverter();

    public static ToyMessageConverter getInstance() {
        return ourInstance;
    }

    private ToyMessageConverter() {
    }


    @Override
    public Object convert2Object(Message message,String address) {
        return message;
    }

    @Override
    public Message convert2Message(Object obj) {
        return (Message) obj;
    }
}
