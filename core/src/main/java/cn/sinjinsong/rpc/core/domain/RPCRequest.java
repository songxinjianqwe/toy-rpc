package cn.sinjinsong.rpc.core.domain;

import lombok.Data;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Data
public class RPCRequest extends Message {

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
