package com.sinjinsong.rpc.sample.spring.server.service.impl;

import com.sinjinsong.rpc.core.annotation.RPCService;
import com.sinjinsong.rpc.sample.spring.api.callback.HelloCallback;
import com.sinjinsong.rpc.sample.spring.api.domain.User;
import com.sinjinsong.rpc.sample.spring.api.service.HelloServiceWithCallback;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@RPCService(callback = true,callbackMethod = "callback",callbackParamIndex = 1)
public class HelloServiceWithCallbackImpl implements HelloServiceWithCallback {
    @Override
    public void hello(User user, HelloCallback callback) {
        callback.callback("Hello, " + user.getUsername());
    }
}
