package com.sinjinsong.toy.sample.spring.client.generic.service;

import com.sinjinsong.toy.config.generic.RPCGenericServiceBean;
import com.sinjinsong.toy.sample.spring.api.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author sinjinsong
 * @date 2018/7/23
 */
@Component
@Slf4j
public class GenericService {
    @Autowired
    @Qualifier("helloService")
    private RPCGenericServiceBean helloService;

    public void test() {

        // 配置 methodName ,parameter types,parameters
        Object result = helloService.invoke("hello", new Class[]{User.class}, new Object[]{new User("1")});
        log.info("generic1:{}",result);
    }
}
