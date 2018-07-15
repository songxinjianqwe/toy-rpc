package com.sinjinsong.toy.registry.api;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public interface ServiceRegistry {
    void init();
    List<String> discover(String interfaceName);
    void register(String address,String interfaceName);
    void close();
}
