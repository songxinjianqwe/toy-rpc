package com.sinjinsong.rpc.core.zookeeper;

import com.sinjinsong.rpc.core.constant.CharsetConst;
import com.sinjinsong.rpc.core.constant.ZookeeperConstant;
import com.sinjinsong.rpc.core.spring.RPCProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by SinjinSong on 2017/9/27.
 * 客户端进行发现
 */
@Slf4j
@Component
public class ServiceDiscovery extends ZookeeperClient {
    @Autowired
    private RPCProperties rpcProperties;
    private volatile List<String> dataList = new ArrayList<>();

  
    @PostConstruct
    public void init() {
        super.connect(address);
        watchNode();
    }

    public String discover() {
        String data = null;
        int size = dataList.size();
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
                log.info("using only data: {}", data);
            } else {
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                log.info("using random data: {}", data);
            }
        }
        return data;
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
            List<String> dataList = new ArrayList<>();
            for (String node : nodeList) {
                byte[] bytes = zookeeper.getData(ZookeeperConstant.ZK_REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(bytes, CharsetConst.UTF_8));
            }
            log.info("node data: {}", dataList);
            this.dataList = dataList;
            log.info("Service discovery triggered updating connected server node.");
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
