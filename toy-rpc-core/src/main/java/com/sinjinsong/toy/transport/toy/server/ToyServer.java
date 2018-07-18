package com.sinjinsong.toy.transport.toy.server;

import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.config.ClusterConfig;
import com.sinjinsong.toy.config.ProtocolConfig;
import com.sinjinsong.toy.config.RegistryConfig;
import com.sinjinsong.toy.transport.api.Server;
import com.sinjinsong.toy.transport.api.constant.FrameConstant;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import com.sinjinsong.toy.transport.toy.RPCTaskRunner;
import com.sinjinsong.toy.transport.api.support.RPCServerHandler;
import com.sinjinsong.toy.transport.toy.codec.RPCDecoder;
import com.sinjinsong.toy.transport.toy.codec.RPCEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by SinjinSong on 2017/7/29.
 */
@Slf4j
public class ToyServer implements Server {
    private RegistryConfig registry;
    private ProtocolConfig protocolConfig;
    private ApplicationConfig applicationConfig;
    private ClusterConfig clusterConfig;
    private ThreadPoolExecutor pool;
    private int threads;

    public ToyServer(ApplicationConfig applicationConfig, ClusterConfig clusterConfig, RegistryConfig registry,
                     ProtocolConfig protocolConfig) {
        this.applicationConfig = applicationConfig;
        this.clusterConfig = clusterConfig;
        this.registry = registry;
        this.protocolConfig = protocolConfig;

        this.threads = protocolConfig.getThreads() != null ? protocolConfig.getThreads() : ProtocolConfig.DEFAULT_THREADS;
        this.pool = new ThreadPoolExecutor(threads, threads, 6L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public void run() {
        //两个事件循环器，第一个用于接收客户端连接，第二个用于处理客户端的读写请求
        //是线程组，持有一组线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //服务器辅助类，用于配置服务器
            ServerBootstrap bootstrap = new ServerBootstrap();
            //配置服务器参数
            bootstrap.group(bossGroup, workerGroup)
                    //使用这种类型的NIO通道，现在是基于TCP协议的
                    .channel(NioServerSocketChannel.class)
                    //对Channel进行初始化，绑定实际的事件处理器，要么实现ChannelHandler接口，要么继承ChannelHandlerAdapter类
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //编码是其他格式转为字节
                            //解码是从字节转到其他格式
                            //服务器是把先请求转为POJO（解码），再把响应转为字节（编码）
                            //而客户端是先把请求转为字节（编码)，再把响应转为POJO（解码）
                            // 在InboundHandler执行完成需要调用Outbound的时候，比如ChannelHandlerContext.write()方法，
                            // Netty是直接从该InboundHandler返回逆序的查找该InboundHandler之前的OutboundHandler，并非从Pipeline的最后一项Handler开始查找
                            ch.pipeline()
                                    .addLast("IdleStateHandler", new IdleStateHandler(10, 0, 0))
                                    // ByteBuf -> Message 
                                    .addLast("LengthFieldPrepender", new LengthFieldPrepender(FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT))
                                    // Message -> ByteBuf
                                    .addLast("RPCEncoder", new RPCEncoder(applicationConfig.getSerializerInstance()))
                                    // ByteBuf -> Message
                                    .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(FrameConstant.MAX_FRAME_LENGTH, FrameConstant.LENGTH_FIELD_OFFSET, FrameConstant.LENGTH_FIELD_LENGTH, FrameConstant.LENGTH_ADJUSTMENT, FrameConstant.INITIAL_BYTES_TO_STRIP))
                                    // Message -> Message
                                    .addLast("RPCDecoder", new RPCDecoder(applicationConfig.getSerializerInstance()))
                                    .addLast("RPCServerHandler", new RPCServerHandler(ToyServer.this, protocolConfig));
                        }
                    })
                    //服务器配置项
                    //BACKLOG
                    //TCP维护有两个队列，分别称为A和B
                    //客户端发送SYN，服务器接收到后发送SYN ACK，将客户端放入到A队列
                    //客户端接收到后再次发送ACK，服务器接收到后将客户端从A队列移至B队列，服务器的accept返回。
                    //A和B队列长度之和为backlog
                    //当A和B队列长度之和大于backlog时，新的连接会被TCP内核拒绝
                    //注意：backlog对程序的连接数并无影响，影响的只是还没有被accept取出的连接数。
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //指定发送缓冲区大小
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    //指定接收缓冲区大小
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024);
            //这里的option是针对于上面的NioServerSocketChannel
            //复杂的时候可能会设置多个Channel
            //.sync表示是一个同步阻塞执行，普通的Netty的IO操作都是异步执行的
            //一个ChannelFuture代表了一个还没有发生的I/O操作。这意味着任何一个请求操作都不会马上被执行
            //Netty强烈建议直接通过添加监听器的方式获取I/O结果，而不是通过同步等待(.sync)的方式
            //如果用户操作调用了sync或者await方法，会在对应的future对象上阻塞用户线程


            //绑定端口，开始监听
            //注意这里可以绑定多个端口，每个端口都针对某一种类型的数据（控制消息，数据消息）
            String host = InetAddress.getLocalHost().getHostAddress();
            ChannelFuture future = bootstrap.bind(host, protocolConfig.getPort()).sync();
            //应用程序会一直等待，直到channel关闭
            log.info("服务器启动");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } finally {
            registry.close();
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void handleRequest(RPCRequest request, ChannelHandlerContext ctx) {
        pool.submit(new RPCTaskRunner(ctx, request, protocolConfig.getProtocolInstance().getExportedServiceConfig(request.getInterfaceName())));
    }
}
