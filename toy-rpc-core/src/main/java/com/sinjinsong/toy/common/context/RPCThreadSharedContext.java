package com.sinjinsong.toy.common.context;


import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sinjinsong
 * @date 2018/6/10
 * 线程共享
 * 不可以使用ThreadLocal来存放，因为发出请求的线程（如main）和接受响应的线程（某个eventloop）不能保证是同一个线程
 */
@Slf4j
public class RPCThreadSharedContext {
    private static final ConcurrentHashMap<String, CompletableFuture<RPCResponse>> RESPONSES = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ServiceConfig<?>> HANDLER_MAP = new ConcurrentHashMap<>();

    public static void registerResponseFuture(String requestId, CompletableFuture<RPCResponse> future) {
        RESPONSES.put(requestId, future);
    }

    public static CompletableFuture<RPCResponse> getAndRemoveResponseFuture(String requestId) {
        return RESPONSES.remove(requestId);
    }

    public static void registerHandler(String name, ServiceConfig serviceConfig) {
        HANDLER_MAP.put(name,
                serviceConfig);
    }

    public static ServiceConfig getAndRemoveHandler(String name) {
        return HANDLER_MAP.remove(name);
    }
}
