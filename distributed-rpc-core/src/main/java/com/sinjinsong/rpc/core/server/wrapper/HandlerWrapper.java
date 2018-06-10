package com.sinjinsong.rpc.core.server.wrapper;

import com.sinjinsong.rpc.core.annotation.RPCService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandlerWrapper {
    private Object handler;
    private RPCService rpcService;
    
    public HandlerWrapper(Object handler) {
        this.handler = handler;
    }
}
