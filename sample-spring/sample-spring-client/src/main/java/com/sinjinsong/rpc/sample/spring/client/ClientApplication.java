package com.sinjinsong.rpc.sample.spring.client;

import com.sinjinsong.rpc.core.annotation.RPCReference;
import com.sinjinsong.rpc.sample.spring.api.domain.User;
import com.sinjinsong.rpc.sample.spring.api.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Slf4j
@SpringBootApplication
@ComponentScan("com.sinjinsong.rpc")
public class ClientApplication implements CommandLineRunner {
    @RPCReference
    HelloService helloService;
    
    public void test() throws Exception {
        log.info(helloService.hello(new User("1")));
        log.info(helloService.hello(new User("2")));
        
        Thread.sleep(3000);
        log.info(helloService.hello(new User("3")));
        Thread.sleep(8000);
        log.info(helloService.hello(new User("4")));
    }
    
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ClientApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String... strings) throws Exception {
        test();
    }
}