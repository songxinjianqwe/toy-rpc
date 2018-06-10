package com.sinjinsong.rpc.core.client.context;

import com.sinjinsong.rpc.core.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sinjinsong
 * @date 2018/6/10
 * 
 * 线程共享
 */
@Slf4j
public class RPCThreadSharedContext {
    private static final ConcurrentHashMap<String, CompletableFuture<RPCResponse>> RESPONSES = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String,Object> HANDLER_MAP = new ConcurrentHashMap<>();
    
    public static void registerResponseFuture(String requestId, CompletableFuture<RPCResponse> future) {
        RESPONSES.put(requestId, future);
    }

    public static CompletableFuture<RPCResponse> getAndRemoveResponseFuture(String requestId) {
        CompletableFuture<RPCResponse> future = RESPONSES.get(requestId);
        RESPONSES.remove(requestId);
        return future;
    }
    
    public static void registerHandler(String name,Object handler) {
        HANDLER_MAP.put(name,handler);
    }
    
    public static Object getAndRemoveHandler(String name) {
         Object handler = HANDLER_MAP.get(name);
         HANDLER_MAP.remove(name);
         return handler;
    }
}
