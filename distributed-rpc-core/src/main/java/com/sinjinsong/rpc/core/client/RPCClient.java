package com.sinjinsong.rpc.core.client;

import com.github.rholder.retry.*;
import com.sinjinsong.rpc.core.coder.RPCDecoder;
import com.sinjinsong.rpc.core.coder.RPCEncoder;
import com.sinjinsong.rpc.core.domain.Message;
import com.sinjinsong.rpc.core.domain.RPCRequest;
import com.sinjinsong.rpc.core.domain.RPCResponseFuture;
import com.sinjinsong.rpc.core.enumeration.ConnectionFailureStrategy;
import com.sinjinsong.rpc.core.exception.ClientConnectionException;
import com.sinjinsong.rpc.core.exception.ServerNotAvailableException;
import com.sinjinsong.rpc.core.zookeeper.ServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.sinjinsong.rpc.core.constant.FrameConstant.*;

/**
 * Created by SinjinSong on 2017/7/29.
 */
@Slf4j
public class RPCClient {
    private ServiceDiscovery discovery;
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel futureChannel;
    private Map<String, RPCResponseFuture> responses;
    private ConnectionFailureStrategy connectionFailureStrategy = ConnectionFailureStrategy.RETRY;

    public RPCClient(ServiceDiscovery discovery) {
        this.discovery = discovery;
        init();
    }
    
    public void init() {
        log.info("初始化RPC客户端");
        this.responses = new ConcurrentHashMap<>();
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

                                .addLast("RPCClientHandler", new RPCClientHandler(RPCClient.this, responses));
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);
        try {
            this.futureChannel = connect();
            log.info("客户端初始化完毕");
        } catch (Exception e) {
            log.error("与服务器的连接出现故障");
            handleException();
        }
    }


    /**
     * 连接失败或IO时失败均会调此方法处理异常
     */
    public void handleException() {
        if (connectionFailureStrategy == ConnectionFailureStrategy.CLOSE) {
            log.info("连接失败策略为直接关闭，关闭客户端");
            this.close();
        } else if (connectionFailureStrategy == ConnectionFailureStrategy.RETRY) {
            log.info("连接失败策略为重新连接，开始重试");
            try {
                this.futureChannel = this.reconnect();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
                log.error("重试过程中抛出异常，关闭客户端");
                this.close();
            } catch (RetryException e) {
                e.printStackTrace();
                log.error("重试次数已达上限，关闭客户端");
                this.close();
            }
        }
    }

    private Channel connect() throws Exception {
        log.info("向ZK查询服务器地址中...");
        String serverAddress = discovery.discover();
        log.info("本次连接的地址为{}",serverAddress);
        if (serverAddress == null) {
            throw new ServerNotAvailableException();
        }
        String host = serverAddress.split(":")[0];
        Integer port = Integer.parseInt(serverAddress.split(":")[1]);
        ChannelFuture future = bootstrap.connect(host, port).sync();
        log.info("客户端已连接");
        return future.channel();
    }

    /**
     * 实现重新连接的重试策略
     *
     * @return
     * @throws ExecutionException
     * @throws RetryException
     */
    private Channel reconnect() throws ExecutionException, RetryException {
        Retryer<Channel> retryer = RetryerBuilder.<Channel>newBuilder()
                .retryIfExceptionOfType(Exception.class) // 抛出IOException时重试 
                .withWaitStrategy(WaitStrategies.incrementingWait(5, TimeUnit.SECONDS, 5, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(5)) // 重试5次后停止  
                .build();
        return retryer.call(() -> {
            log.info("重新连接中...");
            return connect();
        });
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            if (this.futureChannel != null) {
                this.futureChannel.close().sync();
            }
            this.discovery.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * 客户端发送RPC请求
     *
     * @param request
     * @return
     * @throws Exception
     */
    public RPCResponseFuture execute(RPCRequest request) throws Exception {
        if (this.futureChannel == null) {
            throw new ClientConnectionException();
        }
        log.info("客户端发起请求: {}", request);
        RPCResponseFuture responseFuture = new RPCResponseFuture();
        responses.put(request.getRequestId(), responseFuture);
        this.futureChannel.writeAndFlush(Message.buildRequest(request));
        log.info("请求已发送");
        return responseFuture;
    }

}
