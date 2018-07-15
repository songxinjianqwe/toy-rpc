package com.sinjinsong.toy.config;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.protocol.api.Invoker;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;
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
    private Collection<Filter> filters;
    

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
    public static <T> ReferenceConfig<T> createReferenceConfig(Class<T> interfaceClass,
                                                               boolean isAsync,
                                                               boolean isCallback,
                                                               boolean isOneWay,
                                                               long timeout,
                                                               String callbackMethod,
                                                               int callbackParamIndex,
                                                               Collection<Filter> filters) {
        if (CACHE.containsKey(interfaceClass)) {
            if (CACHE.get(interfaceClass).isDiff(isAsync, isCallback, isOneWay, timeout, callbackMethod, callbackParamIndex)) {
                throw new RPCException("同一个接口只能以相同的配置引用" + interfaceClass);
            }
            return (ReferenceConfig<T>) CACHE.get(interfaceClass);
        }
        synchronized (ReferenceConfig.class) {
            if (CACHE.containsKey(interfaceClass)) {
                if (CACHE.get(interfaceClass).isDiff(isAsync, isCallback, isOneWay, timeout, callbackMethod, callbackParamIndex)) {
                    throw new RPCException("同一个接口只能以相同的配置引用" + interfaceClass);
                }
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
                    .filters(filters == null ? Collections.EMPTY_LIST : filters)
                    .build();
            CACHE.put(interfaceClass, config);
            return config;
        }
    }

    private boolean isDiff(boolean isAsync, boolean isCallback, boolean isOneWay, long timeout, String callbackMethod, int callbackParamIndex) {
        if (this.isAsync != isAsync) {
            return true;
        }
        if(this.isCallback != isCallback) {
            return false;
        }
        if(this.isOneWay != isOneWay){
            return true;
        }
        if(this.timeout != timeout) {
            return true;
        }
        if(this.callbackMethod != callbackMethod) {
            return true;
        }
        if(this.callbackParamIndex != callbackParamIndex) {
            return true;
        }
        return false;
    }


    private void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        // ToyInvoker
        invoker = protocolConfig.getProtocolInstance().refer(interfaceClass, this);
        // ClusterInvoker
        invoker = clusterConfig.getLoadBalanceInstance().register(invoker,clusterConfig);
        ref = applicationConfig.getProxyFactoryInstance().createProxy(invoker);
    }

    /**
     * 初始化并
     *
     * @return
     */
    public T get() {
        if (!initialized) {
            init();
        }
        return ref;
    }

}
