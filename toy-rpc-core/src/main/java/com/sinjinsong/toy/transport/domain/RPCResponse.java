package com.sinjinsong.toy.core.transport.domain;

import lombok.Data;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Data
public class RPCResponse {
    private String requestId;
    private Throwable cause;
    private Object result;

    public boolean hasError() {
        return cause != null;
    }
}
