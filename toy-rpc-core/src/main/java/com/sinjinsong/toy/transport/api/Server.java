package com.sinjinsong.toy.transport.api;

import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.config.ClusterConfig;
import com.sinjinsong.toy.config.ProtocolConfig;
import com.sinjinsong.toy.config.RegistryConfig;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public interface Server {
    void run();

    void handleRPCRequest(RPCRequest request, ChannelHandlerContext ctx);

    RegistryConfig getRegistryConfig();

    ProtocolConfig getProtocolConfig();

    ApplicationConfig getApplicationConfig();

    ClusterConfig getClusterConfig();
    
    void close();
}
