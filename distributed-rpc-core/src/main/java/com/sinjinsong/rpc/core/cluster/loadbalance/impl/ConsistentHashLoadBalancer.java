package com.sinjinsong.rpc.core.cluster.loadbalance.impl;

import com.sinjinsong.rpc.core.transport.client.endpoint.Endpoint;
import com.sinjinsong.rpc.core.transport.domain.RPCRequest;
import com.sinjinsong.rpc.core.cluster.loadbalance.AbstractLoadBalancer;
import com.sinjinsong.rpc.core.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
@Slf4j
public class ConsistentHashLoadBalancer extends AbstractLoadBalancer {
    private TreeMap<Long, Endpoint> hashCircle = new TreeMap<>();
    private List<Endpoint> cachedEndpoints;
    private static final int REPLICA_NUMBER = 160;
    

    public ConsistentHashLoadBalancer(ServiceDiscovery serviceDiscovery) {
        super(serviceDiscovery);
    }

    @Override
    protected Endpoint doSelect(List<Endpoint> endpoints, RPCRequest request) {
        if (cachedEndpoints == null || endpoints.hashCode() != cachedEndpoints.hashCode()) {
            buildHashCircle(endpoints);
        }
        if (hashCircle.size() == 0) {
            return null;
        }
        byte[] digest = md5(request.key());
        long hash = hash(digest, 0);
        if (!hashCircle.containsKey(hash)) {
            SortedMap<Long, Endpoint> tailMap = hashCircle.tailMap(hash);
            // tailMap是值大于hash的节点集合
            // 如果是空，那么回到头部
            // 如果非空，那么取大于hash的最近的一个节点
            hash = tailMap.isEmpty() ? hashCircle.firstKey() : tailMap.firstKey();
        }
        return hashCircle.get(hash);
    }
    
    private void buildHashCircle(List<Endpoint> endpoints) {
        if (cachedEndpoints == null) {
            cachedEndpoints = endpoints;
            for (Endpoint endpoint : endpoints) {
                add(endpoint);
            }
        } else {
            
            log.info("旧地址列表为:[}", cachedEndpoints);
            log.info("新地址列表为:{}", endpoints);
            Set<Endpoint> intersect = new HashSet<>(endpoints);
            intersect.retainAll(cachedEndpoints);
            for (Endpoint endpoint : cachedEndpoints) {
                if (!intersect.contains(endpoint)) {
                    remove(endpoint);
                }
            }

            for (Endpoint endpoint : endpoints) {
                if (!intersect.contains(endpoint)) {
                    add(endpoint);
                }
            }
            this.cachedEndpoints = endpoints;
        }
        log.info("更新后地址列表为:{}", new HashSet(hashCircle.values()));
    }
    
    private void add(Endpoint endpoint) {
        for (int i = 0; i < REPLICA_NUMBER / 4; i++) {
            // 根据md5算法为每4个结点生成一个消息摘要，摘要长为16字节128位。
            byte[] digest = md5(endpoint.getAddress() + i);
            // 随后将128位分为4部分，0-31,32-63,64-95,95-128，并生成4个32位数，存于long中，long的高32位都为0
            // 并作为虚拟结点的key。
            for (int h = 0; h < 4; h++) {
                long m = hash(digest, h);
                hashCircle.put(m, endpoint);
            }
        }
    }

    private void remove(Endpoint endpoint) {
        for (int i = 0; i < REPLICA_NUMBER / 4; i++) {
            // 根据md5算法为每4个结点生成一个消息摘要，摘要长为16字节128位。
            byte[] digest = md5(endpoint.getAddress() + i);
            // 随后将128位分为4部分，0-31,32-63,64-95,95-128，并生成4个32位数，存于long中，long的高32位都为0
            // 并作为虚拟结点的key。
            for (int h = 0; h < 4; h++) {
                long m = hash(digest, h);
                hashCircle.remove(m);
            }
        }
    }

    private byte[] md5(String value) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.reset();
        byte[] bytes = null;
        try {
            bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.update(bytes);
        return md5.digest();
    }

    /**
     * digest是128位，共16个byte
     * number为0就是digest 低32位，3是digest 高32位
     * 结果的long的高32位都为0
     * <p>
     * 因为生成的结果是一个32位数，若用int保存可能会产生负数。而一致性hash生成的逻辑环其hashCode的范围是在 0 - MAX_VALUE之间。因此为正整数，所以这里要强制转换为long类型，避免出现负数。
     *
     * @param digest
     * @param number
     * @return
     */
    private long hash(byte[] digest, int number) {
        return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                | (digest[0 + number * 4] & 0xFF))
                & 0xFFFFFFFFL;
    }


}
