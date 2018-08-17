package client;

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
    private ConcurrentTest test;
    
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ClientApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        test.run(100,100 * 500);
//        test.run(1,1);
        System.exit(0);
    }
}