package cn.sinjinsong.rpc.client;

import cn.sinjinsong.rpc.domain.user.User;
import cn.sinjinsong.rpc.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Slf4j
public class RPCProxyTest {
    private RPCProxy proxy = new RPCProxy();
    
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