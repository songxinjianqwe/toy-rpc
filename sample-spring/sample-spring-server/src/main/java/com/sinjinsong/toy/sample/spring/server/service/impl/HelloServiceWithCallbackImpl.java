package com.sinjinsong.toy.sample.spring.server.service.impl;


import com.sinjinsong.toy.sample.spring.api.callback.HelloCallback;
import com.sinjinsong.toy.sample.spring.api.domain.User;
import com.sinjinsong.toy.sample.spring.api.service.HelloServiceWithCallback;
import com.sinjinsong.toy.core.config.annotation.RPCService;

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
