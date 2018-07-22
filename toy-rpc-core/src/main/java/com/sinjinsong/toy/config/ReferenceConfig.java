package com.sinjinsong.toy.config;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.protocol.api.Invoker;
import lombok.Builder;
import lombok.Data;

import java.util.*;
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
    private transient volatile boolean initialized;
//    private transient volatile boolean destroyed;

    private static final Map<Class<?>, ReferenceConfig<?>> CACHE = new ConcurrentHashMap<>();
    private List<Filter> filters;


    /**
     * 非单例，非接口粒度的单例，同一个服务接口多种配置可以对应多个不同的ReferenceConfig
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
                throw new RPCException(ErrorEnum.SAME_INTERFACE_ONLY_CAN_BE_REFERED_IN_THE_SAME_WAY,"同一个接口只能以相同的配置引用:{}", interfaceClass);
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
                .filters(filters == null ? Collections.EMPTY_LIST : new ArrayList(filters))
                .build();
        CACHE.put(interfaceClass, config);
        return config;
    }

    private boolean isDiff(boolean isAsync, boolean isCallback, boolean isOneWay, long timeout, String callbackMethod, int callbackParamIndex) {
        if (this.isAsync != isAsync) {
            return true;
        }
        if (this.isCallback != isCallback) {
            return false;
        }
        if (this.isOneWay != isOneWay) {
            return true;
        }
        if (this.timeout != timeout) {
            return true;
        }
        if (this.callbackMethod != callbackMethod) {
            return true;
        }
        if (this.callbackParamIndex != callbackParamIndex) {
            return true;
        }
        return false;
    }

    private void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        // ClusterInvoker
        Invoker<T> invoker = clusterConfig.getLoadBalanceInstance().register(interfaceClass);
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

    public static ReferenceConfig getReferenceConfigByInterface(Class<?> interfaceClass) {
        return CACHE.get(interfaceClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReferenceConfig<?> that = (ReferenceConfig<?>) o;
        return isAsync == that.isAsync &&
                isOneWay == that.isOneWay &&
                isCallback == that.isCallback &&
                timeout == that.timeout &&
                callbackParamIndex == that.callbackParamIndex &&
                Objects.equals(interfaceName, that.interfaceName) &&
                Objects.equals(interfaceClass, that.interfaceClass) &&
                Objects.equals(callbackMethod, that.callbackMethod) &&
                Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interfaceName, interfaceClass, isAsync, isOneWay, isCallback, timeout, callbackMethod, callbackParamIndex, filters);
    }

    @Override
    public String toString() {
        return "ReferenceConfig{" +
                "interfaceName='" + interfaceName + '\'' +
                ", interfaceClass=" + interfaceClass +
                ", isAsync=" + isAsync +
                ", isOneWay=" + isOneWay +
                ", isCallback=" + isCallback +
                ", timeout=" + timeout +
                ", callbackMethod='" + callbackMethod + '\'' +
                ", callbackParamIndex=" + callbackParamIndex +
                ", ref=" + ref +
                ", initialized=" + initialized +
                ", filters=" + filters +
                '}';
    }
}
