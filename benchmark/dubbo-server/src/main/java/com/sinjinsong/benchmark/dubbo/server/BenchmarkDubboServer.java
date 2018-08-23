package com.sinjinsong.benchmark.dubbo.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.util.concurrent.CountDownLatch;

/**
 * @author sinjinsong
 * @date 2018/8/22
 */
@SpringBootApplication
@ImportResource("classpath:dubbo.xml")
@Slf4j
public class BenchmarkDubboServer implements CommandLineRunner {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication app = new SpringApplication(BenchmarkDubboServer.class);
        app.setWebEnvironment(false);
        app.run(args);
        log.info("server launched");
        new CountDownLatch(1).await();
    }

    @Override
    public void run(String... strings) throws Exception {
        
    }
}