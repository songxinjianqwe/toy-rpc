package com.sinjinsong.toy.cluster.loadbalance;

import com.sinjinsong.toy.cluster.support.AbstractLoadBalancer;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.common.domain.RPCRequest;
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
    private TreeMap<Long, Invoker> hashCircle = new TreeMap<>();
    private List<Invoker> cachedInvokers;
    private static final int REPLICA_NUMBER = 160;

    //TODO check if this works
    @Override
    protected Invoker doSelect(List<Invoker> invokers, RPCRequest request) {
        if (cachedInvokers == null || invokers.hashCode() != cachedInvokers.hashCode()) {
            buildHashCircle(invokers);
        }
        if (hashCircle.size() == 0) {
            return null;
        }
        byte[] digest = md5(request.key());
        long hash = hash(digest, 0);
        if (!hashCircle.containsKey(hash)) {
            SortedMap<Long, Invoker> tailMap = hashCircle.tailMap(hash);
            // tailMap是值大于hash的节点集合
            // 如果是空，那么回到头部
            // 如果非空，那么取大于hash的最近的一个节点
            hash = tailMap.isEmpty() ? hashCircle.firstKey() : tailMap.firstKey();
        }
        return hashCircle.get(hash);
    }

    /**
     * //TODO Invoker 的equals和hashCode可靠吗
     * 目前看起来是用interfaceClass的equals和hashCode
     * @param invokers
     */
    private void buildHashCircle(List<Invoker> invokers) {
        if (cachedInvokers == null) {
            cachedInvokers = invokers;
            for (Invoker invoker : invokers) {
                add(invoker);
            }
        } else {
            
            log.info("旧地址列表为:[}", cachedInvokers);
            log.info("新地址列表为:{}", invokers);
            Set<Invoker> intersect = new HashSet<>(invokers);
            intersect.retainAll(cachedInvokers);
            for (Invoker invoker : cachedInvokers) {
                if (!intersect.contains(invoker)) {
                    remove(invoker);
                }
            }

            for (Invoker invoker : invokers) {
                if (!intersect.contains(invoker)) {
                    add(invoker);
                }
            }
            this.cachedInvokers = invokers;
        }
        log.info("更新后地址列表为:{}", new HashSet(hashCircle.values()));
    }
    
    private void add(Invoker invoker) {
        for (int i = 0; i < REPLICA_NUMBER / 4; i++) {
            // 根据md5算法为每4个结点生成一个消息摘要，摘要长为16字节128位。
            byte[] digest = md5(invoker.getServiceURL().getAddress() + i);
            // 随后将128位分为4部分，0-31,32-63,64-95,95-128，并生成4个32位数，存于long中，long的高32位都为0
            // 并作为虚拟结点的key。
            for (int h = 0; h < 4; h++) {
                long m = hash(digest, h);
                hashCircle.put(m, invoker);
            }
        }
    }

    private void remove(Invoker invoker) {
        for (int i = 0; i < REPLICA_NUMBER / 4; i++) {
            // 根据md5算法为每4个结点生成一个消息摘要，摘要长为16字节128位。
            byte[] digest = md5(invoker.getServiceURL().getAddress() + i);
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
