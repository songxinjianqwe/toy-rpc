package com.sinjinsong.rpc.core.zk;

import com.sinjinsong.rpc.core.constant.CharsetConst;
import com.sinjinsong.rpc.core.constant.ZookeeperConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * Created by SinjinSong on 2017/9/27.
 */
@Slf4j
public class ZookeeperClient {
    /**
     * zk变量
     */
    protected ZooKeeper zookeeper = null;
    /**
     * 信号量设置，用于等待zookeeper连接建立之后 通知阻塞程序继续向下执行
     */
    private CountDownLatch connectedSemaphore = new CountDownLatch(1);
    
    public void connect(String address) {
        try {
            this.zookeeper = new ZooKeeper(address, ZookeeperConstant.ZK_SESSION_TIMEOUT, (WatchedEvent event) -> {
                //获取事件的状态
                Watcher.Event.KeeperState keeperState = event.getState();
                Watcher.Event.EventType eventType = event.getType();
                //如果是建立连接
                if (Watcher.Event.KeeperState.SyncConnected == keeperState) {
                    if (Watcher.Event.EventType.None == eventType) {
                        //如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
                        connectedSemaphore.countDown();
                        log.info("ZK建立连接");
                    }
                }
            });
            log.info("开始连接ZK服务器");
            connectedSemaphore.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNode(String data, String path) {
        try {
            byte[] bytes = data.getBytes(CharsetConst.UTF_8);
            zookeeper.create(path, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info("建立数据节点 ({} => {})", path, data);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 关闭ZK连接
     */
    public void close() {
        try {
            this.zookeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
