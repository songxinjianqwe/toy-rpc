package cn.sinjinsong.rpc.service.Impl;

import cn.sinjinsong.rpc.annotation.RPCService;
import cn.sinjinsong.rpc.domain.user.User;
import cn.sinjinsong.rpc.service.HelloService;

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
