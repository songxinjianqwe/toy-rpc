package com.sinjinsong.toy.common.exception;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public class RPCException extends RuntimeException{
    public RPCException(String message) {
        super(message);
    }
    public RPCException(String message,Throwable cause) {
        super(message,cause);
    }
}
