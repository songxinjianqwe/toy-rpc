package com.sinjinsong.rpc.core.domain;

/**
 * @author sinjinsong
 * @date 2018/3/9
 */
public class MessageCache {
    public static final MessageOuterClass.Message PING = MessageOuterClass.Message
                .newBuilder()
                .setType(MessageOuterClass.Message.MessageType.PING)
                .build();
    public static final MessageOuterClass.Message PONG = MessageOuterClass.Message
                .newBuilder()
                .setType(MessageOuterClass.Message.MessageType.PONG)
                .build();
    
   
}
