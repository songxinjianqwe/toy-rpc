package com.sinjinsong.benchmark.dubbo.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author sinjinsong
 * @date 2018/8/22
 */
@SpringBootApplication
@ImportResource("classpath:dubbo.xml")
@Slf4j
public class BenchmarkDubboClient implements CommandLineRunner {
    @Autowired
    private DubboClient client;

    public static void main(String[] args) throws InterruptedException {
        SpringApplication app = new SpringApplication(BenchmarkDubboClient.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String... strings) throws Exception {
        client.run(strings);
//        client.run(1,1,0,1);
        System.exit(0);
    }
}