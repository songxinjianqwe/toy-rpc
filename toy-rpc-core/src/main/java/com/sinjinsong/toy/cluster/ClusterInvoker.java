package com.sinjinsong.toy.cluster;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ClusterConfig;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.support.AbstractInvoker;
import com.sinjinsong.toy.transport.client.Endpoint;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sinjinsong
 * @date 2018/7/15
 */
@Slf4j
public class ClusterInvoker<T> extends AbstractInvoker<T> {
    
    private ClusterConfig clusterConfig;
    private Map<String,Invoker<T>> addressInvokers = new ConcurrentHashMap<>();
        
    public void setClusterConfig(ClusterConfig clusterConfig) {
        this.clusterConfig = clusterConfig;
    }
    
    public List<Invoker> getInvokers() {
        return new ArrayList<>(addressInvokers.values());
    }

    /**
     * 
     * @param newAddresses
     */
    public void refresh(List<String> newAddresses) {
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
                Invoker invoker = null;
                // 找其他服务的Map中是否包含该endpoint，如果包含，则说明已经连接了，复用该连接即可
//                for (Map<String, Invoker> map : interfaceInvokers.values()) {
//                    if (map != addressEndpoints && map.containsKey(address)) {
//                        invoker = map.get(address);
//                        invoker.getEndpoint().addInterface(interfaceClass.getName());
//                    }
//                }
                // 如果不包含，则建立连接
//                addressInvokers.put(address, invoker != null ? invoker : );
            }
        }
    }
    
    public void close() {
        
    }
    
    @Override
    protected RPCResponse doInvoke(RPCRequest rpcRequest) throws RPCException {
        Invoker invoker = clusterConfig.getLoadBalanceInstance().select(rpcRequest);
        if (invoker != null) {
            // 如果提交任务失败，则删掉该Endpoint，再次提交的话必须重新创建Endpoint
            return invoker.invoke(rpcRequest);
        }
        log.error("未找到可用服务器");
        throw new RPCException("未找到可用服务器");
    }

    @Override
    public Endpoint getEndpoint() {
        throw new UnsupportedOperationException();
    }


    public boolean containsAddress(String newAddress) {
        return addressInvokers.containsKey(newAddress);
    }
}
