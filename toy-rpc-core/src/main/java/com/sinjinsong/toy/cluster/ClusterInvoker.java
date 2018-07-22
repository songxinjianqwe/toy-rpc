package com.sinjinsong.toy.cluster;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.common.util.InvokeParamUtil;
import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.config.ClusterConfig;
import com.sinjinsong.toy.config.ProtocolConfig;
import com.sinjinsong.toy.config.RegistryConfig;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractRemoteInvoker;
import com.sinjinsong.toy.protocol.api.support.InvokerDelegate;
import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author sinjinsong
 * @date 2018/7/15
 * 、代表一个interface的集群，核心类，持有其他cluster组件，如loadbalancer和failureHandler
 */
@Slf4j
public class ClusterInvoker<T> implements Invoker<T> {
    private Class<T> interfaceClass;
    private ClusterConfig clusterConfig;
    private Map<String, Invoker<T>> addressInvokers = new ConcurrentHashMap<>();
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    private ApplicationConfig applicationConfig;

    public ClusterInvoker(Class<T> interfaceClass, ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registryConfig, ProtocolConfig protocolConfig) {
        this.interfaceClass = interfaceClass;
        this.clusterConfig = clusterConfig;
        this.registryConfig = registryConfig;
        this.protocolConfig = protocolConfig;
        this.applicationConfig = applicationConfig;
        init();
    }

    private void init() {
        this.registryConfig.getRegistryInstance().discover(interfaceClass.getName(), (newServiceURLs -> {
            removeNotExisted(newServiceURLs);
        }), (serviceURL -> {
            addOrUpdate(serviceURL);
        }));
    }

    /**
     * addr1,addr2,addr3 -> addr2?weight=20,addr3,addr4
     * <p>
     * 1) addOrUpdate(addr2) -> updateServiceConfig(addr2)
     * 2) addOrUpdate(addr3) -> updateServiceConfig(addr3)
     * 3) addOrUpdate(addr4) -> add(addr4)
     * 4) removeNotExisted(addr2,addr3,addr4) -> remove(addr1)
     *
     * @param serviceURL
     */
    private synchronized void addOrUpdate(ServiceURL serviceURL) {
        // 地址多了/更新
        // 更新
        if (addressInvokers.containsKey(serviceURL.getAddress())) {
            // 怎么更新？
            // 现在invoker类型是封装后的AbstractInvoker类型，未必会对应一个Endpoint
            // 虽然我们知道只有远程服务才有可能会更新
            //TODO refactor this
            Invoker<T> existedInvoker = addressInvokers.get(serviceURL.getAddress());
            if (existedInvoker instanceof InvokerDelegate) {
                Invoker<T> delegatedInvoker = ((InvokerDelegate) existedInvoker).getDelegate();
                if (delegatedInvoker instanceof AbstractRemoteInvoker) {
                    log.info("update config:{}", serviceURL);
                    ((AbstractRemoteInvoker<T>) delegatedInvoker).updateServiceConfig(serviceURL);
                }
            }
        } else {
            // 添加
            log.info("add invoker:{},serviceURL:{}", interfaceClass.getName(), serviceURL);
            Invoker invoker = protocolConfig.getProtocolInstance().refer(interfaceClass);
            // refer拿到的有可能是某一种具体的ProtocolInvoker（远程），也有可能是AbstractInvoker（本地）
            // TODO refactor this
            // 最后是决定，不管一个服务器提供多少个接口，对每个接口建立一个连接，否则管理起来太麻烦
            if (invoker instanceof AbstractRemoteInvoker) {
                invoker = ((AbstractRemoteInvoker) invoker).initEndpoint(serviceURL, applicationConfig);
            }
            addressInvokers.put(serviceURL.getAddress(), invoker);
        }
    }

    public List<Invoker> getInvokers() {
        // 拷贝一份返回
        return new ArrayList<>(addressInvokers.values());
    }

    /**
     * 在该方法调用前，会将新的加进来，所以这里只需要去掉新的没有的。
     * 旧的一定包含了新的，遍历旧的，如果不在新的里面，则需要删掉
     *
     * @param newServiceURLs
     */
    public synchronized void removeNotExisted(List<ServiceURL> newServiceURLs) {
        log.info("registry updated#removeNotExisted");
        Map<String, ServiceURL> newAddressesMap = newServiceURLs.stream().collect(Collectors.toMap(
                url -> url.getAddress(), url -> url
        ));

        // 地址少了
        for (Iterator<Map.Entry<String, Invoker<T>>> it = addressInvokers.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Invoker<T>> curr = it.next();
            if (!newAddressesMap.containsKey(curr.getKey())) {
                log.info("remove address:{}", curr.getKey());
                curr.getValue().close();
                it.remove();
            }
        }
    }

    @Override
    public void close() {
        //TODO 
    }

    @Override
    public Class<T> getInterface() {
        return interfaceClass;
    }

    @Override
    public RPCResponse invoke(InvokeParam invokeParam) throws RPCException {
        Invoker invoker = clusterConfig.getLoadBalanceInstance().select(getInvokers(), InvokeParamUtil.extractRequestFromInvokeParam(invokeParam));
        if (invoker != null) {
            try {
                // 这里只会抛出RPCException
                RPCResponse response = invoker.invoke(invokeParam);
                // response有可能是null，比如callback、oneway和future
                if(response == null) {
                    return null;
                }
                // 不管是传输时候抛异常，还是服务端抛出异常，都算异常
                if (response.hasError()) {
                    throw new RPCException(ErrorEnum.SERVICE_INVOCATION_FAILURE, response.getCause(), "invocation failed");
                }
                // 第一次就OK
                return response;
            } catch (RPCException e) {
                Map<String, Invoker> excludedInvokers = new HashMap<>();
                excludedInvokers.put(invoker.getServiceURL().getAddress(), invoker);
                // 重试后OK
                // 在这里再抛出异常，就没有返回值了
                return clusterConfig.getFaultToleranceHandlerInstance().handle(excludedInvokers, this, invokeParam);
            }
        } else {
            log.error("未找到可用服务器");
            throw new RPCException(ErrorEnum.NO_SERVER_AVAILABLE, "未找到可用服务器");
        }
    }

    /**
     * 这里不需要捕获invoker#invoke的异常，会由retryer来捕获
     *
     * @param excludedInvokers
     * @param invokeParam
     * @return
     */
    public RPCResponse invoke(Map<String, Invoker> excludedInvokers, InvokeParam invokeParam) {
        List<Invoker> invokers = getInvokers();
        for (Iterator<Invoker> it = invokers.iterator(); it.hasNext(); ) {
            if (excludedInvokers.containsKey(it.next().getServiceURL().getAddress())) {
                it.remove();
            }
        }
        Invoker invoker = clusterConfig.getLoadBalanceInstance().select(invokers, InvokeParamUtil.extractRequestFromInvokeParam(invokeParam));
        if (invoker != null) {
            try {
                // 这里只会抛出RPCException
                RPCResponse response = invoker.invoke(invokeParam);
                if(response == null) {
                    return null;
                }
                // 不管是传输时候抛异常，还是服务端抛出异常，都算异常
                if (response.hasError()) {
                    throw new RPCException(ErrorEnum.SERVICE_INVOCATION_FAILURE, response.getCause(), "invocation failed");
                }
                return response;
            } catch (RPCException e) {
                // 再次调用失败，添加到排除列表中
                excludedInvokers.put(invoker.getServiceURL().getAddress(), invoker);
                throw e;
            }
        } else {
            log.error("未找到可用服务器");
            throw new RPCException(ErrorEnum.NO_SERVER_AVAILABLE, "未找到可用服务器");
        }
    }

    @Override
    public ServiceURL getServiceURL() {
        throw new UnsupportedOperationException();
    }
}
