package com.sinjinsong.rpc.sample.spring.server.service.impl;


import com.sinjinsong.rpc.core.annotation.RPCService;
import com.sinjinsong.rpc.sample.spring.api.domain.User;
import com.sinjinsong.rpc.sample.spring.api.service.HelloService;
import org.springframework.stereotype.Service;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@RPCService
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(User user) {
        return "Hello, " + user.getUsername();
    }
}
