package com.sinjinsong.toy.sample.spring.client.call;


import com.sinjinsong.toy.config.annotation.RPCReference;
import com.sinjinsong.toy.sample.spring.api.domain.User;
import com.sinjinsong.toy.sample.spring.api.service.HelloServiceWithCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author sinjinsong
 * @date 2018/6/10
 * 
*/
@Service
@Slf4j
public class CallbackCallService {
    
    @RPCReference(callback = true,callbackMethod = "callback",callbackParamIndex = 1)
    private HelloServiceWithCallback helloServiceWithCallback;

    public void test() throws Exception {
        
        helloServiceWithCallback.hello(new User("1"), result -> {
            log.info("callback1: {}", result);
        });

        helloServiceWithCallback.hello(new User("2"), result -> {
            log.info("callback2: {}", result);
        });

        Thread.sleep(3000);
        helloServiceWithCallback.hello(new User("3"), result -> {
            log.info("callback3: {}", result);
        });

        Thread.sleep(8000);
        helloServiceWithCallback.hello(new User("4"), result -> {
            log.info("callback4: {}", result);
        });

    }
}
