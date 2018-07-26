package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.registry.api.ServiceURL;
import com.sinjinsong.toy.transport.api.Client;
import com.sinjinsong.toy.transport.api.Server;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 为了更好地管理客户端连接，决定把endpoint移到protocol中。一个invoker对应一个endpoint的话是会对一个服务器
 * 多出很多不必要的连接。
 * 一个服务器只需要一个连接即可。
 *
 * @author sinjinsong
 * @date 2018/7/26
 */
@Slf4j
public abstract class AbstractRemoteProtocol extends AbstractProtocol {
    /**
     * key是address，value是连接到该address上的Endpoint
     */
    private Map<String, Client> clients = new ConcurrentHashMap<>();
    private Map<String, Object> locks = new ConcurrentHashMap<>();
    private Server server;
    
    /**
     * 初始化一个客户端
     * 初始化客户端必须放在Protocol，不能放在invoker中，否则无法控制重复初始化的问题。
     *
     * @param serviceURL
     * @return
     */
    public final Client initEndpoint(ServiceURL serviceURL) {
        String address = serviceURL.getAddress();
        locks.putIfAbsent(address, new Object());
        synchronized (locks.get(address)) {
            if (clients.containsKey(address)) {
                return clients.get(address);
            }
            Client client = doInitEndpoint(serviceURL);
            clients.put(address, client);
            locks.remove(address);
            return client;
        }
    }

    /**
     * 更新一个客户端的配置
     *
     * @param serviceURL
     */
    public final void updateEndpointConfig(ServiceURL serviceURL) {
        if (!clients.containsKey(serviceURL.getAddress())) {
            throw new RPCException(ErrorEnum.PROTOCOL_CANNOT_FIND_THE_SERVER_ADDRESS, "无法找到该地址{}", serviceURL);
        }
        clients.get(serviceURL.getAddress()).updateServiceConfig(serviceURL);
    }

    /**
     * 关闭一个客户段
     *
     * @param address
     */
    public final void closeEndpoint(String address) {
        Client client = clients.remove(address);
        if (client != null) {
            log.info("首次关闭客户端:{}", address);
            client.close();
        } else {
            log.info("重复关闭客户端:{}", address);
        }
    }

    protected abstract Client doInitEndpoint(ServiceURL serviceURL);

    protected synchronized final void openServer() {
        if(server == null) {
            server = doOpenServer();
        }
    }

    protected abstract Server doOpenServer();
    
    @Override
    public void close() {
        clients.values().forEach(client -> client.close());
        if(server != null) {
            server.close();
        }
    }
}
