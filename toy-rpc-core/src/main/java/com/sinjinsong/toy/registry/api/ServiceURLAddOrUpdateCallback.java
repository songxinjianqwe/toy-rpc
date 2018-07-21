package com.sinjinsong.toy.registry.api;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */
@FunctionalInterface
public interface ServiceURLAddOrUpdateCallback {
    void addOrUpdate(ServiceURL serviceURL);
}
