package com.sinjinsong.toy.registry.api;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2018/7/16
 */
@FunctionalInterface
public interface ServiceURLRemovalCallback {
    void removeNotExisted(List<ServiceURL> newAddresses);
}
