package com.sinjinsong.toy.transport.api.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Data
public class RPCRequest implements Serializable {
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    
    
    public String key() {
        return new StringBuilder(interfaceName)
                .append(".")
                .append(methodName)
                .append(".")
                .append(Arrays.toString(parameterTypes))
                .append(".")
                .append(Arrays.toString(parameters)).toString();
    }
}
