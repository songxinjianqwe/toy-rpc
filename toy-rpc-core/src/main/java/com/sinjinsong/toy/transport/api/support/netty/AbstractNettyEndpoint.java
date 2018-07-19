package com.sinjinsong.toy.transport.api.support.netty;

import com.sinjinsong.toy.common.context.RPCThreadSharedContext;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.invoke.callback.CallbackInvocation;
import com.sinjinsong.toy.transport.api.MessageConverter;
import com.sinjinsong.toy.transport.api.domain.Message;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import com.sinjinsong.toy.transport.api.support.AbstractEndpoint;
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
public abstract class AbstractNettyEndpoint extends AbstractEndpoint {
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private Channel futureChannel;
    private volatile boolean initialized = false;
    private volatile boolean destroyed = false;
    private MessageConverter converter;

    /**
     * 与Handler相关
     * @return
     */
    protected abstract ChannelInitializer initPipeline();

    /**
     * 与将Message转为Object类型的data相关
     * @return
     */
    protected abstract MessageConverter initConverter();
    
    private void initClient() {
        this.converter = initConverter();
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(initPipeline())
                .option(ChannelOption.SO_KEEPALIVE, true);
        try {
            this.futureChannel = connect();
            log.info("客户端初始化完毕");
        } catch (Exception e) {
            log.error("与服务器的连接出现故障");
            e.printStackTrace();
            handleException(e);
        }
    }

    private Channel connect() throws Exception {
        ChannelFuture future;
        String address = getAddress();
        String host = address.split(":")[0];
        Integer port = Integer.parseInt(address.split(":")[1]);
        future = bootstrap.connect(host, port).sync();
        log.info("客户端已连接至 {}", address);
        return future.channel();
    }


    /**
     * 连接失败或IO时失败均会调此方法处理异常
     */
    @Override
    public void handleException(Throwable throwable) {
        throwable.printStackTrace();
        log.info("连接失败策略为直接关闭，关闭客户端");
        close();
        throw new RPCException("连接失败,关闭客户端");
    }

    @Override
    public void handleCallbackRequest(RPCRequest request, ChannelHandlerContext ctx) {
        // callback
        ServiceConfig serviceConfig = RPCThreadSharedContext.getAndRemoveHandler(
                CallbackInvocation.generateCallbackHandlerKey(request)
        );
        new RPCTaskRunner(ctx, request, serviceConfig,converter).run();
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
            initClient();
            initialized = true;
        }
        if (destroyed) {
            throw new RPCException("当前Endpoint:" + getAddress() + "关闭后仍在提交任务");
        }
        log.info("客户端发起请求: {},请求的服务器为: {}", request, getAddress());
        CompletableFuture<RPCResponse> responseFuture = new CompletableFuture<>();
        RPCThreadSharedContext.registerResponseFuture(request.getRequestId(), responseFuture);
        Object data = converter.convert2Object(Message.buildRequest(request),getAddress());
        log.info("转换后的消息体为:{}",data);
        this.futureChannel.writeAndFlush(data);
        log.info("请求已发送至{}", getAddress());
        return responseFuture;
    }

    /**
     * 如果该Endpoint不提供任何服务，则将其关闭
     */
    @Override
    public void close() {
        try {
            if (this.futureChannel != null) {
                this.futureChannel.close().sync();
            }
            destroyed = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
