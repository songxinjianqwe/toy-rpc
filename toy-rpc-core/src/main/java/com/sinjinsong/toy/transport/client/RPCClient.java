package com.sinjinsong.toy.transport.client;

import com.sinjinsong.toy.cluster.LoadBalancer;
import com.sinjinsong.toy.transport.ServerNotAvailableException;
import com.sinjinsong.toy.transport.client.endpoint.Endpoint;
import com.sinjinsong.toy.transport.domain.RPCRequest;
import com.sinjinsong.toy.transport.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

/**
 * Created by SinjinSong on 2017/7/29.
 */
@Slf4j
public class RPCClient {
    private LoadBalancer loadBalancer;
        
    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                close();
            }
        }));
    }
    
    /**
     * 客户端发送RPC请求
     *
     * @param request
     * @return
     * @throws Exception
     */
    public Future<RPCResponse> execute(RPCRequest request) throws Exception {
        Endpoint endpoint = loadBalancer.select(request);
        if(endpoint != null) {
            return endpoint.submit(request);
        }
        log.error("未找到可用服务器");
        throw new ServerNotAvailableException();
    }

    public void close() {
        loadBalancer.close();
    }
}
