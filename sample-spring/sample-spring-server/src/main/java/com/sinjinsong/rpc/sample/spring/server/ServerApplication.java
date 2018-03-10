package com.sinjinsong.rpc.sample.spring.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author songx
 * @date 2017/7/30
 */
@SpringBootApplication
@ComponentScan({"com.sinjinsong.rpc.sample.spring.server"})
public class ServerApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ServerApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String... strings) throws Exception {
        
    }
}
