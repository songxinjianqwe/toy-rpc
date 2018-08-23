package com.sinjinsong.benchmark.toy.client;

import com.sinjinsong.benchmark.base.client.AbstractClient;
import com.sinjinsong.benchmark.base.service.UserService;
import com.sinjinsong.toy.autoconfig.annotation.RPCReference;
import org.springframework.stereotype.Component;

/**
 * @author sinjinsong
 * @date 2018/8/23
 */
@Component
public class ToyClient extends AbstractClient {
    @RPCReference
    private UserService userService;
    
    @Override
    protected UserService getUserService() {
        return userService;
    }
}
