package com.sinjinsong.toy.sample.spring.injvm;

import com.sinjinsong.toy.sample.spring.injvm.consumer.SyncCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author sinjinsong
 * @date 2018/7/21
 */
@SpringBootApplication
public class Application implements CommandLineRunner {
    @Autowired
    private SyncCallService syncCallService;
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String... strings) throws Exception {
        syncCallService.test();
    }
}
