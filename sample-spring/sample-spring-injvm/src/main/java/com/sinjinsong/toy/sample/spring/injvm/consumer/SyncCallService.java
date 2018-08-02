package com.sinjinsong.toy.sample.spring.injvm.consumer;

import com.sinjinsong.toy.autoconfig.annotation.RPCReference;
import com.sinjinsong.toy.sample.spring.api.domain.User;
import com.sinjinsong.toy.sample.spring.api.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
@Service
public class SyncCallService {
    @RPCReference    
    private HelloService helloService;
    
    public void test() throws Exception {
        log.info("sync:{}",helloService.hello(new User("1")));
        log.info("sync:{}",helloService.hello(new User("2")));
        Thread.sleep(3000);
        log.info("sync:{}",helloService.hello(new User("3")));
        Thread.sleep(8000);
        log.info("sync:{}",helloService.hello(new User("4")));
        
    }
}
