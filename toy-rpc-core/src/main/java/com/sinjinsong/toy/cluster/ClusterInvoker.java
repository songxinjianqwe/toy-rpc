package com.sinjinsong.toy.cluster;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractInvoker;
import com.sinjinsong.toy.transport.client.Endpoint;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author sinjinsong
 * @date 2018/7/15
 */
@Slf4j
public class ClusterInvoker<T> extends AbstractInvoker<T> {
    private ClusterConfig clusterConfig;
    private Map<String, Invoker<T>> addressInvokers = new ConcurrentHashMap<>();
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    private ApplicationConfig applicationConfig;
    private ExecutorService callbackPool;
    
    public ClusterInvoker(Class<T> interfaceClass, ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registryConfig, ProtocolConfig protocolConfig,ExecutorService callbackPool) {
        this.interfaceClass = interfaceClass;
        this.clusterConfig = clusterConfig;
        this.registryConfig = registryConfig;
        this.protocolConfig = protocolConfig;
        this.applicationConfig = applicationConfig;
        this.callbackPool = callbackPool;
        init();
    }

    private void init() {
        this.registryConfig.getRegistryInstance().discover(interfaceClass.getName(),(newAddresses -> {
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
                // 去掉一个Endpoint所管理的一个服务，如果某个Endpoint不管理任何服务，则关闭连接
                addressInvokers.get(address).getEndpoint().closeIfNoServiceAvailable(interfaceClass.getName());
                addressInvokers.remove(address);
            }
        }

        for (String address : newAddresses) {
            if (!intersect.contains(address)) {
                // 最后是决定，不管一个服务器提供多少个接口，对每个接口建立一个连接，否则管理起来太麻烦
                Invoker invoker = protocolConfig.getProtocolInstance().refer(interfaceClass);
                Endpoint endpoint = new Endpoint(address,callbackPool,interfaceClass.getName(),applicationConfig.getSerializerInstance());
                invoker.setEndpoint(endpoint);
                addressInvokers.put(address,invoker);
            }
        }
    }

    public void close() {

    }
    
    @Override
    public Endpoint getEndpoint() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected RPCResponse doInvoke(RPCRequest rpcRequest, ReferenceConfig referenceConfig) throws RPCException {
       Invoker invoker = clusterConfig.getLoadBalanceInstance().select(rpcRequest);
        if (invoker != null) {
            // 如果提交任务失败，则删掉该Endpoint，再次提交的话必须重新创建Endpoint
            return invoker.invoke(rpcRequest,referenceConfig);
        }
        log.error("未找到可用服务器");
        throw new RPCException("未找到可用服务器");
    }
}
