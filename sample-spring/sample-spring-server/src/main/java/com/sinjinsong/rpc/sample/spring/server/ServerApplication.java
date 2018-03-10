package com.sinjinsong.rpc.sample.spring.server;

import com.sinjinsong.rpc.core.server.RPCServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author songx
 * @date 2017/7/30
 */
@SpringBootApplication
@Slf4j
public class ServerApplication implements CommandLineRunner {
    @Autowired
    private RPCServer server;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ServerApplication.class);
        app.setWebEnvironment(false);
        app.run(args);

    }

    @Override
    public void run(String... strings) throws Exception {
        server.run(strings[0]);
    }
}
