package com.sinjinsong.toy.registry.api;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public interface ServiceRegistry {
    void init();
    void discover(String interfaceName, ServiceURLRemovalCallback callback, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback);
    void register(String address,String interfaceName);
    void close();
}
