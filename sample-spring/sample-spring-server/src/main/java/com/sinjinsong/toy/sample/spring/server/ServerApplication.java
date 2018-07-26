package com.sinjinsong.toy.sample.spring.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

/**
 * @author sinjinsong
 * @date 2017/7/30
 */
@SpringBootApplication
@Slf4j
public class ServerApplication implements CommandLineRunner {
    
    public static void main(String[] args) throws InterruptedException {
        SpringApplication app = new SpringApplication(ServerApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
        new CountDownLatch(1).await();
    }

    @Override
    public void run(String... strings) throws Exception {
        
    }
}
