package cn.sinjinsong.rpc.core.client;

import cn.sinjinsong.rpc.core.service.HelloService;
import cn.sinjinsong.sample.api.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Slf4j
public class RPCProxyTest {
    private RPCProxy proxy = new RPCProxy("192.168.1.118:2181,192.168.1.119:2181,192.168.1.120:2181");
    
    @Test
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