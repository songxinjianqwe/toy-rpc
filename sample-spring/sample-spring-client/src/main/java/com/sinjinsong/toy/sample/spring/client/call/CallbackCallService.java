package com.sinjinsong.toy.sample.spring.client.call;


import com.sinjinsong.toy.autoconfig.annotation.RPCReference;
import com.sinjinsong.toy.sample.spring.api.domain.User;
import com.sinjinsong.toy.sample.spring.api.service.HelloServiceWithCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sinjinsong
 * @date 2018/6/10
 * 
*/
@Component
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
    
    public void concurrentTest() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 20; i++) {
            String currentUsername = String.valueOf(i + 1);
            pool.submit(() -> {
                helloServiceWithCallback.hello(new User(currentUsername), result -> {
                    log.info("callback{}:{}", currentUsername, result);
                });
            });
        }
    }
    
    public void testOnceCall() throws Exception {
        helloServiceWithCallback.hello(new User("1"), result -> {
            log.info("callback1: {}", result);
        });

    }
}
