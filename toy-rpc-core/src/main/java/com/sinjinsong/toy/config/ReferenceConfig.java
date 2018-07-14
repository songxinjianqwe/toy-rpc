package com.sinjinsong.toy.config;

import com.sinjinsong.toy.protocol.api.Invoker;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sinjinsong
 * @date 2018/7/7
 * <p>
 * 引用服务配置
 */
@Data
@Builder
public class ReferenceConfig<T> extends AbstractConfig {
    private String interfaceName;
    private Class<T> interfaceClass;
    private boolean isAsync;
    private boolean isOneWay;
    private boolean isCallback;
    private long timeout = 3000;
    private String callbackMethod;
    private int callbackParamIndex = 1;
    
    private transient volatile T ref;
    private transient volatile Invoker<T> invoker;
    private transient volatile boolean initialized;
//    private transient volatile boolean destroyed;

    private static final Map<Class<?>, ReferenceConfig<?>> CACHE = new ConcurrentHashMap<>();
    

    /**
     * 同一个接口只会对应一个ReferenceConfig实例
     *
     * @param interfaceClass
     * @param isAsync
     * @param isCallback
     * @param timeout
     * @param callbackMethod
     * @param callbackParamIndex
     * @param <T>
     * @return
     */
    public static <T> ReferenceConfig<T> getSingletonByInterfaceName(Class<T> interfaceClass,
                                                                     boolean isAsync,
                                                                     boolean isCallback,
                                                                     boolean isOneWay,
                                                                     long timeout,
                                                                     String callbackMethod,
                                                                     int callbackParamIndex) {
        if (CACHE.containsKey(interfaceClass)) {
            return (ReferenceConfig<T>) CACHE.get(interfaceClass);
        }
        synchronized (ReferenceConfig.class) {
            if (CACHE.containsKey(interfaceClass)) {
                return (ReferenceConfig<T>) CACHE.get(interfaceClass);
            }
            ReferenceConfig config = ReferenceConfig.builder()
                    .interfaceName(interfaceClass.getName())
                    .interfaceClass((Class<Object>) interfaceClass)
                    .isAsync(isAsync)
                    .isCallback(isCallback)
                    .isOneWay(isOneWay)
                    .timeout(timeout)
                    .callbackMethod(callbackMethod)
                    .callbackParamIndex(callbackParamIndex)
                    .build();
            CACHE.put(interfaceClass, config);
            return config;
        }
    }


    private void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        invoker = protocolConfig.getProtocolInstance().refer(interfaceClass,this);
        ref = applicationConfig.getProxyFactoryInstance().createProxy(invoker);
    }

    /**
     * 初始化并
     * @return
     */
    public T get() {
        if (!initialized) {
            init();
        }
        return ref;
    }
    
}
