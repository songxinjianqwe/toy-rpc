package com.sinjinsong.toy.sample.spring.server.service.impl;


import com.sinjinsong.toy.config.annotation.RPCService;
import com.sinjinsong.toy.sample.spring.api.domain.User;
import com.sinjinsong.toy.sample.spring.api.service.HelloService;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@RPCService
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(User user) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(Math.random() > 0.5) {
            throw new RuntimeException("provider side error");
        }
        return "Hello, " + user.getUsername();
    }
}
