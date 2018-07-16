package com.sinjinsong.toy.registry.api;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/7/16
 */
@FunctionalInterface
public interface ClusterCallback {
    void addresseChanged(List<String> newAddresses);
}
