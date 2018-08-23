package com.sinjinsong.benchmark.dubbo.client;

import com.sinjinsong.benchmark.base.client.AbstractClient;
import com.sinjinsong.benchmark.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author sinjinsong
 * @date 2018/8/22
 */
@Component
public class DubboClient extends AbstractClient {
    @Autowired 
    private UserService userService;
    
    @Override
    protected UserService getUserService() {
        return userService;
    }
}
