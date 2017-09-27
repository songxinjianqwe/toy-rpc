package cn.sinjinsong.rpc.sample.client;


import cn.sinjinsong.rpc.core.client.RPCProxy;
import cn.sinjinsong.sample.api.domain.User;
import cn.sinjinsong.sample.api.service.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Slf4j
public class RPCProxyTest {
    private RPCProxy proxy = new RPCProxy("192.168.1.118:2181,192.168.1.119:2181,192.168.1.120:2181");
    
    
    public void create() throws Exception {
        HelloService helloService = proxy.create(HelloService.class);
        log.info(helloService.hello(new User("1")));
        log.info(helloService.hello(new User("2")));
        
        Thread.sleep(3000);
        log.info(helloService.hello(new User("3")));
        Thread.sleep(8000);
        log.info(helloService.hello(new User("4")));
        
        proxy.close();
    }

}