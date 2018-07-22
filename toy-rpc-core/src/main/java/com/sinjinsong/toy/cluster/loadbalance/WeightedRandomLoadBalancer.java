package com.sinjinsong.toy.cluster.loadbalance;

import com.sinjinsong.toy.cluster.support.AbstractLoadBalancer;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */
@Slf4j
public class WeightedRandomLoadBalancer extends AbstractLoadBalancer {
    /**
     * 每个invoker有一个权值，从0~100，默认值均为100
     * 假设有n个invoker，第i个invoker的权值为weight[i]。那么随机到该invoker的概率为
     * weight[i]/sigma 0->n(weight[i])
     * 
     * 比如说有4个invoker，权值分别为1,2,3,4
     * 随机一个值，范围为[0,10)
     * 如果是在[0,1) -> invoker[0]
     * 如果是在[1,3) -> invoker[1]
     * 如果是在[3,6) -> invoker[2]
     * 如果是在[6.10) -> invoker[3]
     * @param invokers
     * @param request
     * @return
     */
    @Override
    protected Invoker doSelect(List<Invoker> invokers, RPCRequest request) {
        int sum = invokers.stream().mapToInt(invoker -> Integer.parseInt(invoker.getServiceURL().getKey(ServiceURL.Key.WEIGHT).get(0))).sum();
        // 值不包含sum，所以最后一定有一个小于0的
        int randomValue = ThreadLocalRandom.current().nextInt(sum);
        for (Invoker invoker : invokers) {
            int currentWeight = Integer.parseInt(invoker.getServiceURL().getKey(ServiceURL.Key.WEIGHT).get(0));
            log.info("invoker:{},weight:{}",invoker.getServiceURL().getAddress(),currentWeight);
            randomValue -= currentWeight;
            if(randomValue < 0) {
                return invoker;
            }
        }
        return null;
    }
}
