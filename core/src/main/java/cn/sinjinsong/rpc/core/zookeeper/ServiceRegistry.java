package cn.sinjinsong.rpc.core.zookeeper;

import cn.sinjinsong.rpc.core.constant.CharsetConst;
import cn.sinjinsong.rpc.core.constant.ZookeeperConstant;
import cn.sinjinsong.rpc.core.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

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
   

    public ServiceRegistry() {
        String address = PropertyUtil.getProperty("registry.address");
        if (address == null) {
            throw new IllegalStateException("registry.address未找到");
        }
        try {
            this.zk = new ZooKeeper(address, ZookeeperConstant.ZK_SESSION_TIMEOUT, new Watcher() {
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

    public void register(String data) {
        try {
            Stat s = zk.exists(ZookeeperConstant.ZK_REGISTRY_PATH, false);
            if (s == null) {
                zk.create(ZookeeperConstant.ZK_REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            createNode(zk, data);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes(CharsetConst.UTF_8);
            String path = zk.create(ZookeeperConstant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

}
