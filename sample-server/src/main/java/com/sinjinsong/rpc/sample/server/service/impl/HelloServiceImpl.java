package com.sinjinsong.rpc.sample.server.service.impl;


import com.sinjinsong.rpc.core.annotation.RPCService;
import com.sinjinsong.rpc.sample.api.domain.User;
import com.sinjinsong.rpc.sample.api.service.HelloService;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@RPCService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(User user) {
        return "Hello, " + user.getUsername();
    }
}
