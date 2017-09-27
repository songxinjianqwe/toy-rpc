package cn.sinjinsong.rpc.core.client;

import cn.sinjinsong.rpc.core.coder.RPCDecoder;
import cn.sinjinsong.rpc.core.coder.RPCEncoder;
import cn.sinjinsong.rpc.core.domain.RPCRequest;
import cn.sinjinsong.rpc.core.domain.RPCResponseFuture;
import cn.sinjinsong.rpc.core.registry.ServiceRegistry;
import cn.sinjinsong.rpc.core.domain.RPCResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SinjinSong on 2017/7/29.
 */
@Slf4j
public class RPCClient {
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel futureChannel;
    private Map<String, RPCResponseFuture> responses;
    private ServiceRegistry registry;
    public RPCClient(String registryAddress) {
        log.info("初始化RPC客户端");
        responses = new ConcurrentHashMap<>();
        registry = new ServiceRegistry(registryAddress);
        
    }
    
    public void run() {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new IdleStateHandler(0, 0, 5))
                                .addLast(new RPCEncoder(RPCRequest.class)) // 将 RPC 请求进行编码（为了发送请求）
                                .addLast(new RPCDecoder(RPCResponse.class)) // 将 RPC 响应进行解码（为了处理响应）
                                .addLast(new RPCClientHandler(responses)); // 使用 RpcClient 发送 RPC 请求
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);
        connect();
        log.info("客户端初始化完毕");
    }
    
    public void connect() {
        try {
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
            this.futureChannel = future.channel();
            log.info("客户端已连接");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            this.futureChannel.close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public RPCResponseFuture execute(RPCRequest request) throws Exception {
        if (!futureChannel.isActive()) {
            connect();
        }
        log.info("客户端发起请求: {}", request);
        RPCResponseFuture responseFuture = new RPCResponseFuture();
        responses.put(request.getRequestId(), responseFuture);
        this.futureChannel.writeAndFlush(request);
        log.info("请求已发送");
        return responseFuture;
    }


}
