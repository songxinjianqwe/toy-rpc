package com.sinjinsong.toy.transport.api.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Data
public class RPCResponse implements Serializable {
    private String requestId;
    private Throwable cause;
    private Object result;

    public boolean hasError() {
        return cause != null;
    }
}
