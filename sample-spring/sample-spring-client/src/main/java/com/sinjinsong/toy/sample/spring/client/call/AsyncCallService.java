package com.sinjinsong.toy.sample.spring.client.call;

import com.sinjinsong.toy.sample.spring.api.domain.User;
import com.sinjinsong.toy.sample.spring.api.service.HelloService;
import com.sinjinsong.toy.transport.client.context.RPCThreadLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
@Service
public class AsyncCallService {
//    @RPCReference(async=true)  
    private HelloService helloService;
    
    public void test() throws Exception {
        helloService.hello(new User("1"));
        Future<String> hello1Future = RPCThreadLocalContext.getContext().getFuture();
        log.info("{}",hello1Future.get());
        
        
        helloService.hello(new User("2"));
        Future<String> hello2Future = RPCThreadLocalContext.getContext().getFuture();
        log.info("{}",hello2Future.get());
        
        Thread.sleep(3000);
        helloService.hello(new User("3"));
        Future<String> hello3Future = RPCThreadLocalContext.getContext().getFuture();
        log.info("{}",hello3Future.get());
        
        
        Thread.sleep(8000);
        helloService.hello(new User("4"));
        Future<String> hello4Future = RPCThreadLocalContext.getContext().getFuture();
        log.info("{}",hello4Future.get());
    }
}
