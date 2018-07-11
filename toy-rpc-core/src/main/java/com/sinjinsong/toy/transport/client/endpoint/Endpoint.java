package com.sinjinsong.toy.transport.client.endpoint;

import com.sinjinsong.toy.rpc.api.RPCThreadSharedContext;
import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.transport.FrameConstant;
import com.sinjinsong.toy.transport.client.handler.RPCClientHandler;
import com.sinjinsong.toy.transport.codec.RPCDecoder;
import com.sinjinsong.toy.transport.codec.RPCEncoder;
import com.sinjinsong.toy.transport.domain.Message;
import com.sinjinsong.toy.transport.domain.RPCRequest;
import com.sinjinsong.toy.transport.domain.RPCResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
public class Endpoint {
    private String address;
    private List<String> interfaces;
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private Channel futureChannel;
    private volatile boolean initialized = false;
    private ExecutorService callbackPool;
    private Serializer serializer;
    
    public Endpoint(String address, ExecutorService callbackPool,String interfaceName,Serializer serializer) {
        this.callbackPool = callbackPool;
        this.address = address;
        this.interfaces = new ArrayList<>();
        this.interfaces.add(interfaceName);
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

                                .addLast("RPCClientHandler", new RPCClientHandler(Endpoint.this, callbackPool));
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
        String host = address.split(":")[0];
        Integer port = Integer.parseInt(address.split(":")[1]);
        ChannelFuture future = bootstrap.connect(host, port).sync();
        log.info("客户端已连接至 {}", this.address);
        return future.channel();
    }

    /**
     * 连接失败或IO时失败均会调此方法处理异常
     */

    public void handleException() {
        log.info("连接失败策略为直接关闭，关闭客户端");
        close();
    }


    /**
     * 提交请求
     * @param request
     * @return
     */
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
    public void closeIfNoServiceAvailable(String interfaceName) {
        interfaces.remove(interfaceName);
        if (interfaces.size() == 0) {
            close();
        }
    }

    /**
     * 如果该Endpoint不提供任何服务，则将其关闭
     */
    public void close() {
        try {
            if (this.futureChannel != null) {
                this.futureChannel.close().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
    
    public String getAddress() {
        return address;
    }

    public void addInterface(String interfaceName) {
        this.interfaces.add(interfaceName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return initialized == endpoint.initialized &&
                Objects.equals(address, endpoint.address) &&
                Objects.equals(interfaces, endpoint.interfaces) &&
                Objects.equals(bootstrap, endpoint.bootstrap) &&
                Objects.equals(group, endpoint.group) &&
                Objects.equals(futureChannel, endpoint.futureChannel) &&
                Objects.equals(callbackPool, endpoint.callbackPool);
    }

    @Override
    public int hashCode() {

        return Objects.hash(address, interfaces, bootstrap, group, futureChannel, initialized, callbackPool);
    }
}
