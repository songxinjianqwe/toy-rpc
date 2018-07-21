package com.sinjinsong.toy.sample.spring.client;

import com.sinjinsong.toy.sample.spring.client.call.AsyncCallService;
import com.sinjinsong.toy.sample.spring.client.call.CallbackCallService;
import com.sinjinsong.toy.sample.spring.client.call.SyncCallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Slf4j
@SpringBootApplication
public class ClientApplication implements CommandLineRunner {
    @Autowired
    private SyncCallService syncCallService;
    @Autowired
    private AsyncCallService asyncCallService;
    @Autowired
    private CallbackCallService callbackCallService;
    
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ClientApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        syncCallService.concurrentTest();
//        syncCallService.test();
//        asyncCallService.test();
//        callbackCallService.test();
    }
}