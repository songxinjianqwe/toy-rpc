package com.sinjinsong.toy.sample.spring.client.call;

import com.sinjinsong.toy.config.annotation.RPCReference;
import com.sinjinsong.toy.sample.spring.api.domain.User;
import com.sinjinsong.toy.sample.spring.api.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
@Component
public class SyncCallService {
    @RPCReference
    private HelloService helloService;

    public void testOnceCall() throws Exception {
        log.info("sync:{}", helloService.hello(new User("1")));
    }

    public void test() throws Exception {
        log.info("sync:{}", helloService.hello(new User("1")));
        log.info("sync:{}", helloService.hello(new User("2")));

        Thread.sleep(3000);
        log.info("sync:{}", helloService.hello(new User("3")));
        Thread.sleep(8000);
        log.info("sync:{}", helloService.hello(new User("4")));
    }

    public void concurrentTest() {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 20; i++) {
            String currentUsername = String.valueOf(i + 1);
            pool.submit(() -> {
                log.info("sync:{}", helloService.hello(new User(currentUsername)));
            });
        }
    }
}
