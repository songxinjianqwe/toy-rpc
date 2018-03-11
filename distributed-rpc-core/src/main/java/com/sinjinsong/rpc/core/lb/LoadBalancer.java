package com.sinjinsong.rpc.core.lb;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */
public interface LoadBalancer {
    String get(String clientAddress);
    void update(List<String> addresses);
}
