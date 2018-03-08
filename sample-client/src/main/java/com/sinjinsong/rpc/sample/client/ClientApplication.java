package com.sinjinsong.rpc.sample.client;


import com.sinjinsong.rpc.core.client.RPCClient;
import com.sinjinsong.rpc.sample.api.domain.User;
import com.sinjinsong.rpc.sample.api.service.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Slf4j
public class ClientApplication {
    private RPCClient client = new RPCClient();
    
    public void create() throws Exception {
        HelloService helloService = client.createProxy(HelloService.class);
        log.info(helloService.hello(new User("1")));
        log.info(helloService.hello(new User("2")));
        
        Thread.sleep(3000);
        log.info(helloService.hello(new User("3")));
        Thread.sleep(8000);
        log.info(helloService.hello(new User("4")));
    }
        
    public static void main(String[] args) throws Exception {
        new ClientApplication().create();
    }
}