package com.sinjinsong.toy.sample.spring.server.service.impl;


import com.sinjinsong.toy.config.annotation.RPCService;
import com.sinjinsong.toy.sample.spring.api.callback.HelloCallback;
import com.sinjinsong.toy.sample.spring.api.domain.User;
import com.sinjinsong.toy.sample.spring.api.service.HelloServiceWithCallback;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@RPCService(callback = true,callbackMethod = "callback",callbackParamIndex = 1)
public class HelloServiceWithCallbackImpl implements HelloServiceWithCallback {
    @Override
    public void hello(User user, HelloCallback callback) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        throw new RuntimeException("provider side error");
        callback.callback("Hello, " + user.getUsername());
    }
}
