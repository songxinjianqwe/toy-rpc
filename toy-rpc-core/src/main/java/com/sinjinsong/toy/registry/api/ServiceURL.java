package com.sinjinsong.toy.registry.api;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */
@Slf4j
@EqualsAndHashCode(of = {"address"})
@ToString
public final class ServiceURL {
    private String address;
    private volatile Map<Key, List<String>> params = new HashMap<>();

    public static ServiceURL DEFAULT_SERVICE_URL;

    static {
        try {
            DEFAULT_SERVICE_URL = new ServiceURL(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取地址
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * 是否存在该key
     *
     * @param key
     * @return
     */
    public boolean containsKey(Key key) {
        return params.containsKey(key);
    }

    /**
     * 获取key对应的value，如果不存在，则返回默认值，默认值至少是一个空的List，不会是null
     *
     * @param key
     * @return
     */
    public List<String> getKey(Key key) {
        return params.containsKey(key) ? key.getDefaultValues() : params.get(key);
    }

    private ServiceURL() {
    }
    
    private ServiceURL(String address) {
        this.address = address;
    }
    
    
    public static ServiceURL parse(String data) {
        ServiceURL serviceURL = new ServiceURL();
        String[] urlSlices = data.split("\\?");
        serviceURL.address = urlSlices[0];
        //解析URL参数
        if (urlSlices.length > 1) {
            String params = urlSlices[1];
            String[] urlParams = params.split("&");
            if (serviceURL.params == null) {
                serviceURL.params = new HashMap<>();
            }
            for (String param : urlParams) {
                String[] kv = param.split("=");
                String key = kv[0];
                Key keyEnum = Key.valueOf(key.toUpperCase());
                if (keyEnum != null) {
                    String[] values = kv[1].split(",");
                    serviceURL.params.put(keyEnum, Arrays.asList(values));
                } else {
                    log.error("key {} 不存在 ", key);
                }
            }
        }
        return serviceURL;
    }


    public enum Key {
        WEIGHT(Arrays.asList("100"));
        private List<String> defaultValues;

        Key() {

        }

        Key(List<String> defaultValues) {
            this.defaultValues = defaultValues;
        }

        public List<String> getDefaultValues() {
            return defaultValues == null ? Collections.EMPTY_LIST : defaultValues;
        }
    }
}
