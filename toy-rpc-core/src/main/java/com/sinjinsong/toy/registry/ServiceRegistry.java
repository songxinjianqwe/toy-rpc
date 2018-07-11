package com.sinjinsong.toy.registry;

import com.sinjinsong.toy.common.constant.CharsetConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by SinjinSong on 2017/9/27.
 * 服务器进行服务注册或者客户端进行服务发现
 */
@Slf4j
public class ServiceRegistry extends ZookeeperClient {
    private static long TEN_SEC = 10000000000L;

    private volatile Thread discoveringThread;
    private volatile Map<String, List<String>> addresses = new ConcurrentHashMap<>();

    public ServiceRegistry(String registryAddress) {
        super.connect(registryAddress);
    }

    /**
     * 服务发现
     * 返回值的key是接口名，返回值的value是IP地址列表
     *
     * @return
     */
    public List<String> discover(String interfaceName) {
        log.info("discovering...");
        // 如果是第一次discovering，那么watchNode
        if (this.discoveringThread == null) {
            this.discoveringThread = Thread.currentThread();
            watchNode(interfaceName);
            log.info("开始Park... ");
            LockSupport.parkNanos(this, TEN_SEC);
            log.info("Park结束");
        }
        return addresses.get(interfaceName);
    }

    /**
     * 数据格式：
     * /toy/AService/192.168.1.1:1221 -> 192.168.1.1:1221
     * /toy/AService/192.168.1.2:1221 -> 192.168.1.2:1221
     * /toy/BService/192.168.1.3:1221 -> 192.168.1.3:1221
     */
    private void watchNode(String interfaceName) {
        try {
            List<String> interfaceNames = zookeeper.getChildren(ZookeeperConstant.ZK_REGISTRY_PATH, false);
            
            for (String i : interfaceNames) {
                String path = generatePath(interfaceName);
                if (i.equals(interfaceName)) {
                    List<String> addresses = zookeeper.getChildren(path, new Watcher() {
                        @Override
                        public void process(WatchedEvent event) {
                            if (event.getType() == Event.EventType.NodeChildrenChanged) {
                                watchNode(interfaceName);
                            }
                        }
                    });
                    log.info("interfaceName:{} -> addresses:{}", interfaceName, addresses);
                    List<String> dataList = new ArrayList<>();
                    for (String node : addresses) {
                        byte[] bytes = zookeeper.getData(path + "/" + node, false, null);
                        dataList.add(new String(bytes, CharsetConst.UTF_8));
                    }
                    log.info("node data: {}", dataList);
                    this.addresses.put(interfaceName, dataList);
                }
            }
            LockSupport.unpark(discoveringThread);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务注册
     *
     * @param address
     * @param interfaces
     */
    public void register(String address, Set<String> interfaces) {
        for (String interfaceName : interfaces) {
            String path = generatePath(interfaceName);
            createPathIfAbsent(path, CreateMode.PERSISTENT);
            createNode(address, path);
        }
    }

    private static String generatePath(String interfaceName) {
        return new StringBuilder(ZookeeperConstant.ZK_REGISTRY_PATH).append("/").append(interfaceName).toString();
    }
}
