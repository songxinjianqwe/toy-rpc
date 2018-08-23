package com.sinjinsong.toy.transport.api.converter;

import com.sinjinsong.toy.common.domain.Message;

/**
 * @author sinjinsong
 * @date 2018/7/21
 */
public interface MessageConverter {
    Object convert2Object(Message message);
    Message convert2Message(Object obj);
}
