package com.sinjinsong.toy.transport.client.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sinjinsong
 * @date 2018/6/10
 * 注意活跃度的计算，在三种客户端调用方式是不一样的。
 * 同步是在请求发送前inc，自己get后dec
 * 异步是在请求发送前inc，用户get后dec
 * callback是在请求发送前inc，接收到服务器发来的request时dec
 */
public class RPCStatus {
    private static final Map<String, Integer> ACTIVE_COUNT = new ConcurrentHashMap<>();

    public static int getCount(String interfaceName, String methodName, String address) {
        return ACTIVE_COUNT.get(generateKey(interfaceName, methodName, address));
    }
    
    public static void incCount(String interfaceName, String methodName, String address) {
        String key = generateKey(interfaceName, methodName, address);
        if (ACTIVE_COUNT.containsKey(key)) {
            ACTIVE_COUNT.put(key, ACTIVE_COUNT.get(key) + 1);
        } else {
            ACTIVE_COUNT.put(key, Integer.valueOf(1));
        }
    }
    
    public static void decCount(String interfaceName, String methodName, String address) {
        String key = generateKey(interfaceName, methodName, address);
        ACTIVE_COUNT.put(key, ACTIVE_COUNT.get(key) - 1);
    }

    private static String generateKey(String interfaceName, String methodName, String address) {
        return new StringBuilder(interfaceName).append(".").append(methodName).append(".").append(address).toString();
    }
}
