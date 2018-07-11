package com.sinjinsong.toy.transport.server.wrapper;

import com.sinjinsong.toy.config.ServiceConfig;
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
    private ServiceConfig serviceConfig;
    
    public HandlerWrapper(Object handler) {
        this.handler = handler;
    }
}
