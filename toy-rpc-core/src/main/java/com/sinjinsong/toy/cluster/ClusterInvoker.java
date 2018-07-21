package com.sinjinsong.toy.cluster;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.common.util.InvokeParamUtil;
import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.config.ClusterConfig;
import com.sinjinsong.toy.config.ProtocolConfig;
import com.sinjinsong.toy.config.RegistryConfig;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractInvoker;
import com.sinjinsong.toy.transport.api.Endpoint;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sinjinsong
 * @date 2018/7/15
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
        this.registryConfig.getRegistryInstance().discover(interfaceClass.getName(), (newAddresses -> {
            refresh(newAddresses);
        }));
    }

    public List<Invoker> getInvokers() {
        return new ArrayList<>(addressInvokers.values());
    }

    /**
     * @param newAddresses
     */
    public synchronized void refresh(List<String> newAddresses) {
        Set<String> oldAddresses = addressInvokers.keySet();

        Set<String> intersect = new HashSet<>(newAddresses);
        intersect.retainAll(oldAddresses);

        for (String address : oldAddresses) {
            if (!intersect.contains(address)) {
                // 只要让服务器进入debug，就会从zk中移除，然后就会触发这段代码
                addressInvokers.get(address).close();
                addressInvokers.remove(address);
            }
        }

        for (String address : newAddresses) {
            if (!intersect.contains(address)) {
                // 最后是决定，不管一个服务器提供多少个接口，对每个接口建立一个连接，否则管理起来太麻烦
                Invoker invoker = protocolConfig.getProtocolInstance().refer(interfaceClass);
                Endpoint endpoint = protocolConfig.getProtocolInstance().openClient(address, applicationConfig);
                // TODO refactor this
                ((AbstractInvoker) invoker).setEndpoint(endpoint);
                addressInvokers.put(address, invoker);
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
        Invoker invoker = clusterConfig.getLoadBalanceInstance().select(InvokeParamUtil.extractRequestFromInvokeParam(invokeParam));
        if (invoker != null) {
            return invoker.invoke(invokeParam);
        }
        log.error("未找到可用服务器");
        throw new RPCException("未找到可用服务器");
    }
    
    @Override
    public String getAddress() {
        throw new UnsupportedOperationException();
    }
}
