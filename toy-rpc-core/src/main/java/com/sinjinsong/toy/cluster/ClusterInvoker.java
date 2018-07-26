package com.sinjinsong.toy.cluster;

import com.sinjinsong.toy.common.context.RPCThreadLocalContext;
import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.common.util.InvokeParamUtil;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractRemoteProtocol;
import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author sinjinsong
 * @date 2018/7/15
 * 、代表一个interface的集群，核心类，持有其他cluster组件，如loadbalancer和failureHandler
 * 一个接口的一个地址可以定位到一个invoker，但只需要一个地址就可以定位到endpoint
 * invoker与endpoint不是一一对应的
 */
@Slf4j
public class ClusterInvoker<T> implements Invoker<T> {
    private Class<T> interfaceClass;
    private String interfaceName;
    /**
     * key是address，value是一个invoker
     */
    private Map<String, Invoker<T>> addressInvokers = new ConcurrentHashMap<>();
    private ClusterConfig clusterConfig;
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    private ApplicationConfig applicationConfig;


    public ClusterInvoker(Class<T> interfaceClass, String interfaceName, ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registryConfig, ProtocolConfig protocolConfig) {
        this.interfaceClass = interfaceClass;
        this.interfaceName = interfaceName;
        this.clusterConfig = clusterConfig;
        this.registryConfig = registryConfig;
        this.protocolConfig = protocolConfig;
        this.applicationConfig = applicationConfig;
        init();
    }

    private void init() {
        this.registryConfig.getRegistryInstance().discover(interfaceName, (newServiceURLs -> {
            removeNotExisted(newServiceURLs);
        }), (serviceURL -> {
            addOrUpdate(serviceURL);
        }));
    }

    /**
     * addr1,addr2,addr3 -> addr2?weight=20,addr3,addr4
     * <p>
     * 1) addOrUpdate(addr2) -> updateEndpointConfig(addr2)
     * 2) addOrUpdate(addr3) -> updateEndpointConfig(addr3)
     * 3) addOrUpdate(addr4) -> add(addr4)
     * 4) removeNotExisted(addr2,addr3,addr4) -> remove(addr1)
     *
     * @param serviceURL
     */
    private synchronized void addOrUpdate(ServiceURL serviceURL) {
        // 地址多了/更新
        // 更新
        if (addressInvokers.containsKey(serviceURL.getAddress())) {
            // 我们知道只有远程服务才有可能会更新
            // 更新配置与invoker无关，只需要Protocol负责
            //TODO refactor this

            if (protocolConfig.getProtocolInstance() instanceof AbstractRemoteProtocol) {
                AbstractRemoteProtocol protocol = (AbstractRemoteProtocol) protocolConfig.getProtocolInstance();
                log.info("update config:{},当前interface为:{}", serviceURL,interfaceName);
                protocol.updateEndpointConfig(serviceURL);
            }
        } else {
            // 添加
            // 需要修改
            log.info("add invoker:{},serviceURL:{}", interfaceName, serviceURL);
            Invoker invoker = protocolConfig.getProtocolInstance().refer(ReferenceConfig.getReferenceConfigByInterfaceName(interfaceName), serviceURL);
            // refer拿到的是InvokerDelegate
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
        Map<String, ServiceURL> newAddressesMap = newServiceURLs.stream().collect(Collectors.toMap(
                url -> url.getAddress(), url -> url
        ));

        // 地址少了
        // 说明一个服务器挂掉了或出故障了，我们需要把该服务器对应的所有invoker都关掉。
        for (Iterator<Map.Entry<String, Invoker<T>>> it = addressInvokers.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Invoker<T>> curr = it.next();
            if (!newAddressesMap.containsKey(curr.getKey())) {
                log.info("remove address:{},当前interface为:{}", curr.getKey(),interfaceName);
                if (protocolConfig.getProtocolInstance() instanceof AbstractRemoteProtocol) {
                    AbstractRemoteProtocol protocol = (AbstractRemoteProtocol) protocolConfig.getProtocolInstance();
                    protocol.closeEndpoint(curr.getKey());
                }
                it.remove();
            }
        }
    }

    @Override
    public Class<T> getInterface() {
        return interfaceClass;
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * 从可用的invoker中选择一个，如果没有或者不可用，则抛出异常
     *
     * @param availableInvokers
     * @param invokeParam
     * @return
     */
    private Invoker doSelect(List<Invoker> availableInvokers, InvokeParam invokeParam) {
        if (availableInvokers.size() == 0) {
            log.error("未找到可用服务器");
            throw new RPCException(ErrorEnum.NO_SERVER_AVAILABLE, "未找到可用服务器");
        }
        Invoker invoker;
        if (availableInvokers.size() == 1) {
            invoker = availableInvokers.get(0);
            if (invoker.isAvailable()) {
                return invoker;
            } else {
                log.error("未找到可用服务器");
                throw new RPCException(ErrorEnum.NO_SERVER_AVAILABLE, "未找到可用服务器");
            }
        }
        invoker = clusterConfig.getLoadBalanceInstance().select(availableInvokers, InvokeParamUtil.extractRequestFromInvokeParam(invokeParam));
        if (invoker.isAvailable()) {
            return invoker;
        } else {
            availableInvokers.remove(invoker);
            return doSelect(availableInvokers, invokeParam);
        }
    }

    @Override
    public RPCResponse invoke(InvokeParam invokeParam) throws RPCException {
        Invoker invoker = doSelect(getInvokers(), invokeParam);
        RPCThreadLocalContext.getContext().setInvoker(invoker);
        try {
            // 这里只会抛出RPCException
            RPCResponse response = invoker.invoke(invokeParam);
            // response有可能是null，比如callback、oneway和future
            if (response == null) {
                return null;
            }
            // 不管是传输时候抛异常，还是服务端抛出异常，都算异常
            if (response.hasError()) {
                throw new RPCException(response.getCause(), ErrorEnum.SERVICE_INVOCATION_FAILURE, "invocation failed");
            }
            // 第一次就OK
            return response;
        } catch (RPCException e) {
            // 重试后OK
            // 在这里再抛出异常，就没有返回值了
            return clusterConfig.getFaultToleranceHandlerInstance().handle(this, invokeParam, e);
        }
    }

    /**
     * 这里不需要捕获invoker#invoke的异常，会由retryer来捕获
     *
     * @param availableInvokers
     * @param invokeParam
     * @return
     */
    public RPCResponse invokeForFaultTolerance(List<Invoker> availableInvokers, InvokeParam invokeParam) {
        Invoker invoker = doSelect(availableInvokers, invokeParam);
        RPCThreadLocalContext.getContext().setInvoker(invoker);
        // 这里只会抛出RPCException
        RPCResponse response = invoker.invoke(invokeParam);
        if (response == null) {
            return null;
        }
        // 不管是传输时候抛异常，还是服务端抛出异常，都算异常
        if (response.hasError()) {
            throw new RPCException(response.getCause(), ErrorEnum.SERVICE_INVOCATION_FAILURE, "invocation failed");
        }
        return response;
    }

    @Override
    public ServiceURL getServiceURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
