package com.sinjinsong.toy.core.transport.client.endpoint;

import com.sinjinsong.rpc.core.transport.client.context.RPCThreadSharedContext;
import com.sinjinsong.rpc.core.transport.client.handler.RPCClientHandler;
import com.sinjinsong.rpc.core.transport.coder.RPCDecoder;
import com.sinjinsong.rpc.core.transport.coder.RPCEncoder;
import com.sinjinsong.rpc.core.transport.domain.Message;
import com.sinjinsong.rpc.core.transport.domain.RPCRequest;
import com.sinjinsong.rpc.core.transport.domain.RPCResponse;
import com.sinjinsong.toy.core.transport.client.context.RPCThreadSharedContext;
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
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static com.sinjinsong.rpc.core.transport.FrameConstant.*;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
public class Endpoint {
    private String address;
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private Channel futureChannel;
    private volatile boolean initialized = false;
    private ThreadPoolExecutor pool;
    
    public Endpoint(String address, ThreadPoolExecutor pool) {
        this.pool = pool;
        this.address = address;
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
                                .addLast("LengthFieldPrepender", new LengthFieldPrepender(LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT))
                                // Message -> ByteBuf
                                .addLast("RPCEncoder", new RPCEncoder())
                                // ByteBuf -> Message
                                .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP))
                                // Message -> Message
                                .addLast("RPCDecoder", new RPCDecoder())

                                .addLast("RPCClientHandler", new RPCClientHandler(Endpoint.this,pool));
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
        this.close();
    }
   


    /**
     * 提交请求
     *
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
     * 关闭连接
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return Objects.equals(address, endpoint.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "address='" + address + '\'' +
                '}';
    }
}
