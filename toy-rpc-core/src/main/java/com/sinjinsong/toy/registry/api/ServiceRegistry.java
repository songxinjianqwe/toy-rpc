package com.sinjinsong.toy.registry.api;

import java.util.List;
import java.util.Set;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public interface ServiceRegistry {
    void init();
    List<String> discover(String interfaceName);
    void register(String address, Set<String> interfaces);
    void close();
}
