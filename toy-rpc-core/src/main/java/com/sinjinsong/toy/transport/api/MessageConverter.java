package com.sinjinsong.toy.transport.api;

import com.sinjinsong.toy.transport.api.domain.Message;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public interface MessageConverter {
    Object convert2Object(Message message,String address);
    Message convert2Message(Object obj);
}
