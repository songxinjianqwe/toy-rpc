package com.sinjinsong.toy.sample.spring.server.service.impl;


import com.sinjinsong.toy.sample.spring.api.domain.User;
import com.sinjinsong.toy.sample.spring.api.service.HelloService;
import com.sinjinsong.toy.core.config.annotation.RPCService;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@RPCService
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(User user) {
        return "Hello, " + user.getUsername();
    }
}
