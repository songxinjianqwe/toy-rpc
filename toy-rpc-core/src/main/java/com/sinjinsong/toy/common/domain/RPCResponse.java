package com.sinjinsong.toy.common.domain;

import io.netty.util.Recycler;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by SinjinSong on 2017/7/30.
 * 
 * 几种使用场景：
 * 1）consumer收到响应时创建，返回给用户前被回收(反序列化无法reuse，只能recycle)
 * 2）provider服务调用完毕后创建，序列化传输给consumer后被回收/injvm返回用户前被回收
 * 
 */
@Data
public class RPCResponse implements Serializable {
    private final transient Recycler.Handle<RPCResponse> handle;
    private String requestId;
    private Throwable cause;
    private Object result;
    
    public RPCResponse(Recycler.Handle<RPCResponse> handle) {
        this.handle = handle;
    }
    
    public boolean hasError() {
        return cause != null;
    }
    
    public void recycle() {
        requestId = null;
        cause = null;
        result = null;
        handle.recycle(this);
    }
}
