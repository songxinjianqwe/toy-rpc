package com.sinjinsong.toy.cluster.loadbalance;

import com.sinjinsong.toy.cluster.support.AbstractLoadBalancer;
import com.sinjinsong.toy.common.context.RPCStatus;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/3/15
 * 对异步和回调方式作用不明显，主要是用于同步调用方式
 */
@Slf4j
public class LeastActiveLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected Invoker doSelect(List<Invoker> invokers, RPCRequest request) {
        Invoker target = null;
        int least = 0;
        for (Invoker invoker : invokers) {
            int current = RPCStatus.getCount(request.getInterfaceName(), request.getMethodName(), invoker.getServiceURL().getAddress());
            log.info("requestId:" + request.getRequestId() + ",invoker:{},count:{}", invoker.getServiceURL().getAddress(), Integer.valueOf(current));
            if (target == null || current < least) {
                target = invoker;
                least = current;
            }
        }
        return target;
    }
}
