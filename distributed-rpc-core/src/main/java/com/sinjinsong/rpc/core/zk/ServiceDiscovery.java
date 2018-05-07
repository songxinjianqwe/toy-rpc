package com.sinjinsong.rpc.core.zk;

import com.sinjinsong.rpc.core.constant.CharsetConst;
import com.sinjinsong.rpc.core.constant.ZookeeperConstant;
import com.sinjinsong.rpc.core.lb.LoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by SinjinSong on 2017/9/27.
 * 客户端进行发现
 */
@Slf4j
public class ServiceDiscovery extends ZookeeperClient {
    private LoadBalancer loadBalancer;
    private Thread discoveringThread;
    private static long TEN_SEC = 10000000000L;
    public ServiceDiscovery(String registryAddress, LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
        super.connect(registryAddress);
    }
    
    public String discover(String clientAddress) {
        log.info("discovering...");
        // 如果是第一次discovering，那么watchNode
        if(this.discoveringThread == null) {
            this.discoveringThread = Thread.currentThread();
            watchNode();
        }
        log.info("开始Park... ");
        LockSupport.parkNanos(this, TEN_SEC);
        log.info("Park结束");
        return loadBalancer.get(clientAddress);
    }

    private void watchNode() {
        try {
            List<String> nodeList = zookeeper.getChildren(ZookeeperConstant.ZK_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        watchNode();
                    }
                }
            });
            log.info("node list: {}",nodeList);
            List<String> dataList = new ArrayList<>();
            for (String node : nodeList) {
                byte[] bytes = zookeeper.getData(ZookeeperConstant.ZK_REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(bytes, CharsetConst.UTF_8));
            }
            log.info("node data: {}", dataList);
            loadBalancer.update(dataList);
            LockSupport.unpark(discoveringThread);
            log.info("Service discovery triggered updating connected client node.");
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
