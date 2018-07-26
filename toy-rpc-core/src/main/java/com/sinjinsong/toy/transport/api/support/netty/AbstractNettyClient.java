package com.sinjinsong.toy.transport.api.support.netty;

import com.sinjinsong.toy.common.context.RPCThreadSharedContext;
import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.invocation.callback.CallbackInvocation;
import com.sinjinsong.toy.transport.api.converter.ClientMessageConverter;
import com.sinjinsong.toy.transport.api.converter.MessageConverter;
import com.sinjinsong.toy.transport.api.domain.Message;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import com.sinjinsong.toy.transport.api.support.AbstractClient;
import com.sinjinsong.toy.transport.api.support.RPCTaskRunner;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
@Slf4j
public abstract class AbstractNettyClient extends AbstractClient {
    private Bootstrap bootstrap;
    private Channel futureChannel;
    private EventLoopGroup group;
    private volatile boolean initialized = false;
    private volatile boolean destroyed = false;
    private MessageConverter converter;
    
    /**
     * 与Handler相关
     *
     * @return
     */
    protected abstract ChannelInitializer initPipeline();

    /**
     * 与将Message转为Object类型的data相关
     *
     * @return
     */
    protected abstract ClientMessageConverter initConverter();

    @Override
    public boolean isAvailable() {
        return initialized && !destroyed;
    }
        
    @Override
    protected synchronized void connect() {
        if (initialized) {
            return;
        }
        this.converter = initConverter();
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(initPipeline())
                .option(ChannelOption.SO_KEEPALIVE, true);
        try {
            ChannelFuture future;
            String address = getServiceURL().getAddress();
            String host = address.split(":")[0];
            Integer port = Integer.parseInt(address.split(":")[1]);
            future = bootstrap.connect(host, port).sync();
            this.futureChannel = future.channel();
            log.info("客户端已连接至 {}", address);
            log.info("客户端初始化完毕");
            initialized = true;
        } catch (Exception e) {
            log.error("与服务器的连接出现故障");
            e.printStackTrace();
            handleException(e);
        }
    }


    /**
     * 连接失败或IO时失败均会调此方法处理异常
     */
    @Override
    public void handleException(Throwable throwable) {
        log.info("连接失败策略为直接关闭，关闭客户端");
        log.error("",throwable);
        close();
        throw new RPCException(ErrorEnum.CONNECT_TO_SERVER_FAILURE, "连接失败,关闭客户端");
    }

    @Override
    public void handleCallbackRequest(RPCRequest request, ChannelHandlerContext ctx) {
        // callback
        ServiceConfig serviceConfig = RPCThreadSharedContext.getAndRemoveHandler(
                CallbackInvocation.generateCallbackHandlerKey(request)
        );
        getGlobalConfig().getClientExecutor()
                .submit(new RPCTaskRunner(ctx, request, serviceConfig, converter));
    }

    @Override
    public void handleRPCResponse(RPCResponse response) {
        CompletableFuture<RPCResponse> future = RPCThreadSharedContext.getAndRemoveResponseFuture(response.getRequestId());
        future.complete(response);
    }

    /**
     * 提交请求
     *
     * @param request
     * @return
     */
    @Override
    public Future<RPCResponse> submit(RPCRequest request) {
        if (!initialized) {
            connect();
            initialized = true;
        }
        if (destroyed) {
            throw new RPCException(ErrorEnum.SUBMIT_AFTER_ENDPOINT_CLOSED, "当前Endpoint: {} 关闭后仍在提交任务", getServiceURL().getAddress());
        }
        log.info("客户端发起请求: {},请求的服务器为: {}", request, getServiceURL().getAddress());
        CompletableFuture<RPCResponse> responseFuture = new CompletableFuture<>();
        RPCThreadSharedContext.registerResponseFuture(request.getRequestId(), responseFuture);
        Object data = converter.convert2Object(Message.buildRequest(request));
        this.futureChannel.writeAndFlush(data);
        log.info("请求已发送至{}", getServiceURL().getAddress());
        return responseFuture;
    }

    /**
     * 如果该Endpoint不提供任何服务，则将其关闭
     * 要做成幂等的，因为多个invoker都对应一个endpoint，当某个服务器下线时，可能会有多个interface（ClusterInvoker）
     * 都检测到地址变更，所以会关闭对应的invoker。
     */
    @Override
    public void close() {
        try {
            if (this.futureChannel != null && futureChannel.isOpen()) {
                this.futureChannel.close().sync();
            }
            destroyed = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (group != null && !group.isShuttingDown() && !group.isShutdown() && !group.isTerminated()) {
                group.shutdownGracefully();
            }
        }
    }
}
