package com.sinjinsong.toy.sample.spring.client.generic;

import com.sinjinsong.toy.config.bean.RPCGenericServiceBean;
import com.sinjinsong.toy.sample.spring.api.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author sinjinsong
 * @date 2018/7/23
 */
@Component
public class GenericService {
    @Autowired
    @Qualifier("helloService")
    private RPCGenericServiceBean helloService;

    public void test() {

        // 配置 methodName ,parameter types,parameters
        helloService.invoke("hello", new Class[]{User.class}, new Object[]{new User("1")});
    }
}
