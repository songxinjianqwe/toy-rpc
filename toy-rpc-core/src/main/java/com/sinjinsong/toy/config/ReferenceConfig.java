package com.sinjinsong.toy.config;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.RPCInvokeParam;
import com.sinjinsong.toy.transport.api.domain.GlobalRecycler;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class ReferenceConfig<T> extends AbstractConfig {
    private String interfaceName;
    private Class<T> interfaceClass;
    private boolean isAsync;
    private boolean isOneWay;
    private boolean isCallback;
    private long timeout = 3000;
    private String callbackMethod;
    private int callbackParamIndex = 1;
    private boolean isGeneric;
    private volatile Invoker<T> invoker;
    private volatile T ref;
    private volatile boolean initialized;

    private static final Map<String, ReferenceConfig<?>> CACHE = new ConcurrentHashMap<>();
    private List<Filter> filters;


    /**
     * 非单例，非接口粒度的单例，同一个服务接口多种配置可以对应多个不同的ReferenceConfig
     *
     * @param interfaceName
     * @param isAsync
     * @param isCallback
     * @param timeout
     * @param callbackMethod
     * @param callbackParamIndex
     * @param <T>
     * @return
     */
    public static <T> ReferenceConfig<T> createReferenceConfig(String interfaceName,
                                                               /** 参数可控*/
                                                               Class<T> interfaceClass,
                                                               boolean isAsync,
                                                               boolean isCallback,
                                                               boolean isOneWay,
                                                               long timeout,
                                                               String callbackMethod,
                                                               int callbackParamIndex,
                                                               boolean isGeneric,
                                                               List<Filter> filters) {
        if (CACHE.containsKey(interfaceName)) {
            if (CACHE.get(interfaceName).isDiff(isAsync, isCallback, isOneWay, timeout, callbackMethod, callbackParamIndex, isGeneric)) {
                throw new RPCException(ErrorEnum.SAME_INTERFACE_ONLY_CAN_BE_REFERRED_IN_THE_SAME_WAY, "同一个接口只能以相同的配置引用:{}", interfaceName);
            }
            return (ReferenceConfig<T>) CACHE.get(interfaceName);
        }

        ReferenceConfig config = ReferenceConfig.builder()
                .interfaceName(interfaceName)
                .interfaceClass((Class<Object>) interfaceClass)
                .isAsync(isAsync)
                .isCallback(isCallback)
                .isOneWay(isOneWay)
                .timeout(timeout)
                .callbackMethod(callbackMethod)
                .callbackParamIndex(callbackParamIndex)
                .isGeneric(isGeneric)
                .filters(filters != null ? filters : new ArrayList<>())
                .build();
        CACHE.put(interfaceName, config);
        return config;
    }


    private boolean isDiff(boolean isAsync, boolean isCallback, boolean isOneWay, long timeout, String callbackMethod, int callbackParamIndex, boolean isGeneric) {
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
        if (!this.callbackMethod.equals(callbackMethod)) {
            return true;
        }
        if (this.callbackParamIndex != callbackParamIndex) {
            return true;
        }
        if (this.isGeneric != isGeneric) {
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
        invoker = getClusterConfig().getLoadBalanceInstance().referCluster(this);
        if (!isGeneric) {
            ref = getApplicationConfig().getProxyFactoryInstance().createProxy(invoker);
        }
    }


    public Object invokeForGeneric(String methodName, Class<?>[] parameterTypes, Object[] args) {
        if (!initialized) {
            init();
        }
        if (isGeneric) {
            RPCRequest request = GlobalRecycler.reuse(RPCRequest.class);
            log.info("调用泛化服务：{} {}", interfaceName, methodName);
            request.setRequestId(UUID.randomUUID().toString());
            request.setInterfaceName(interfaceName);
            request.setMethodName(methodName);
            request.setParameterTypes(parameterTypes);
            request.setParameters(args);
            // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
            // ClusterInvoker
            RPCInvokeParam invokeParam = RPCInvokeParam.builder()
                    .rpcRequest(request)
                    .referenceConfig(this)
                    .build();
            RPCResponse response = invoker.invoke(invokeParam);
            if (response == null) {
                // callback,oneway,async
                return null;
            } else {
                return response.getResult();
            }
        } else {
            throw new RPCException(ErrorEnum.GENERIC_INVOCATION_ERROR, "只有泛化调用的refernce才可以调用invoke方法");
        }
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

    public static ReferenceConfig getReferenceConfigByInterfaceName(String interfaceName) {
        return CACHE.get(interfaceName);
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
                initialized == that.initialized &&
                Objects.equals(interfaceName, that.interfaceName) &&
                Objects.equals(interfaceClass, that.interfaceClass) &&
                Objects.equals(callbackMethod, that.callbackMethod) &&
                Objects.equals(ref, that.ref) &&
                Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {

        return Objects.hash(interfaceName, interfaceClass, isAsync, isOneWay, isCallback, timeout, callbackMethod, callbackParamIndex, ref, initialized, filters);
    }
}
