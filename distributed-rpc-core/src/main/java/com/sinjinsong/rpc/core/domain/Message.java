package com.sinjinsong.rpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by SinjinSong on 2017/7/31.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private byte type;
    private RPCRequest request;
    private RPCResponse response;
    
    public Message(byte type) {
        this.type = type;
    }

    public static Message buildRequest(RPCRequest request) {
        return new Message(REQUEST,request,null);
    }

    public static Message buildResponse(RPCResponse response) {
        return new Message(RESPONSE,null,response);
    }
    
    public static final byte PING = 1 << 0;
    public static final byte PONG = 1 << 1;
    public static final byte REQUEST = 1 << 2;
    public static final byte RESPONSE = 1 << 3;
    public static final Message PING_MSG = new Message(PING);
    public static final Message PONG_MSG = new Message(PONG);
}
