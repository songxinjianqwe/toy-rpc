package cn.sinjinsong.rpc.core.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by SinjinSong on 2017/9/27.
 */
@Slf4j
public class ServiceRegistry {
    /**
     * zk变量
     */
    private ZooKeeper zk = null;
    /**
     * 信号量设置，用于等待zookeeper连接建立之后 通知阻塞程序继续向下执行
     */
    private CountDownLatch connectedSemaphore = new CountDownLatch(1);
    /**
     * 定义session失效时间
     */
    private static final int SESSION_TIMEOUT = 10000;
   
    public ServiceRegistry(String registryAddress){
        this.close();
        try {
            this.zk = new ZooKeeper(registryAddress, SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    //获取事件的状态
                    Event.KeeperState keeperState = event.getState();
                    Event.EventType eventType = event.getType();
                    //如果是建立连接
                    if (Event.KeeperState.SyncConnected == keeperState) {
                        if (Event.EventType.None == eventType) {
                            //如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
                            connectedSemaphore.countDown();
                            log.info("ZK建立连接");
                        }
                    }
                }
            });
            log.info("开始连接ZK服务器");
            connectedSemaphore.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭ZK连接
     */
    public void close() {
        if (this.zk != null) {
            try {
                this.zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
