package com.sinjinsong.toy.transport.toy.client;

import com.sinjinsong.toy.common.context.RPCThreadSharedContext;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.invoke.callback.CallbackInvocation;
import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.transport.api.Endpoint;
import com.sinjinsong.toy.transport.api.support.RPCClientHandler;
import com.sinjinsong.toy.transport.toy.codec.RPCDecoder;
import com.sinjinsong.toy.transport.toy.codec.RPCEncoder;
import com.sinjinsong.toy.transport.api.constant.FrameConstant;
import com.sinjinsong.toy.transport.api.domain.Message;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import com.sinjinsong.toy.transport.toy.RPCTaskRunner;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


/**
 * @author sinjinsong
 * @date 2018/6/10
 * 相当于一个客户端连接，对应一个Channel
 * 每个服务器的每个接口对应一个Endpoint
 */
@Slf4j
public class ToyEndpoint implements Endpoint {
    private String address;
    private String interfaceName;
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private Channel futureChannel;
    private volatile boolean initialized = false;
    private volatile boolean destroyed = false;
    private ExecutorService callbackPool;
    private Serializer serializer;

    public ToyEndpoint(String address, ExecutorService callbackPool, String interfaceName, Serializer serializer) {
        this.callbackPool = callbackPool;
        this.address = address;
        this.interfaceName = interfaceName;
        this.serializer = serializer;
    }

    private void init() {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast("IdleStateHandler", new IdleStateHandler(0, 5, 0))
                                // ByteBuf -> Message 
                                .addLast("LengthFieldPrepender", new LengthFieldPrepender(FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT))
                                // Message -> ByteBuf
                                .addLast("RPCEncoder", new RPCEncoder(serializer))
                                // ByteBuf -> Message
                                .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(FrameConstant.MAX_FRAME_LENGTH, FrameConstant.LENGTH_FIELD_OFFSET, FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT, FrameConstant.INITIAL_BYTES_TO_STRIP))
                                // Message -> Message
                                .addLast("RPCDecoder", new RPCDecoder(serializer))

                                .addLast("RPCClientHandler", new RPCClientHandler(ToyEndpoint.this));
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);
        try {
            this.futureChannel = connect();
            log.info("客户端初始化完毕");
        } catch (Exception e) {
            log.error("与服务器的连接出现故障");
            e.printStackTrace();
            handleException();
        }
    }

    private Channel connect() throws Exception {
        ChannelFuture future = null;
        String host = address.split(":")[0];
        Integer port = Integer.parseInt(address.split(":")[1]);
        future = bootstrap.connect(host, port).sync();
        log.info("客户端已连接至 {}", this.address);
        return future.channel();
    }

    
    
    
    /**
     * 连接失败或IO时失败均会调此方法处理异常
     */
    @Override
    public void handleException() {
        log.info("连接失败策略为直接关闭，关闭客户端");
        close();
        throw new RPCException("连接失败,关闭客户端");
    }

    @Override
    public void handleRequest(RPCRequest request, ChannelHandlerContext ctx) {
        // callback
        ServiceConfig serviceConfig = RPCThreadSharedContext.getAndRemoveHandler(
                CallbackInvocation.generateCallbackHandlerKey(request)
        );
        callbackPool.submit(new RPCTaskRunner(ctx, request, serviceConfig));
    }

    @Override
    public void handleResponse(RPCResponse response) {
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
            init();
            initialized = true;
        }
        log.info("客户端发起请求: {},请求的服务器为: {}", request, address);
        CompletableFuture<RPCResponse> responseFuture = new CompletableFuture<>();
        RPCThreadSharedContext.registerResponseFuture(request.getRequestId(), responseFuture);
        this.futureChannel.writeAndFlush(Message.buildRequest(request));
        log.info("请求已发送至{}", address);
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

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToyEndpoint endpoint = (ToyEndpoint) o;
        return initialized == endpoint.initialized &&
                destroyed == endpoint.destroyed &&
                Objects.equals(address, endpoint.address) &&
                Objects.equals(interfaceName, endpoint.interfaceName) &&
                Objects.equals(bootstrap, endpoint.bootstrap) &&
                Objects.equals(group, endpoint.group) &&
                Objects.equals(futureChannel, endpoint.futureChannel) &&
                Objects.equals(callbackPool, endpoint.callbackPool) &&
                Objects.equals(serializer, endpoint.serializer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, interfaceName, bootstrap, group, futureChannel, initialized, destroyed, callbackPool, serializer);
    }

}
