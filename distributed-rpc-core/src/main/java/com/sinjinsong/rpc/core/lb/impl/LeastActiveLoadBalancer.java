package com.sinjinsong.rpc.core.lb.impl;

import com.sinjinsong.rpc.core.lb.LoadBalancer;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/3/15
 */
public class LeastActiveLoadBalancer implements LoadBalancer {
    @Override
    public String get(String clientAddress) {
        return null;
    }

    @Override
    public void update(List<String> addresses) {

    }
}
