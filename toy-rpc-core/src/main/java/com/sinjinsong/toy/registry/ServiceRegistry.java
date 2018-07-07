package com.sinjinsong.toy.core.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

/**
 * Created by SinjinSong on 2017/9/27.
 * 服务器进行注册
 */
@Slf4j
public class ServiceRegistry extends ZookeeperClient {
    
    public ServiceRegistry(String registryAddress) {
        super.connect(registryAddress);
    }
    
    public void register(String data) {
        try {
            Stat s = zookeeper.exists(ZookeeperConstant.ZK_REGISTRY_PATH, false);
            if (s == null) {
                zookeeper.create(ZookeeperConstant.ZK_REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            createNode(data, ZookeeperConstant.ZK_DATA_PATH);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
