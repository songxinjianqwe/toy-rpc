# 基于Netty、ProtoStuff、Zookeeper实现的分布式RPC框架
## 博客
参见https://blog.csdn.net/songxinjianqwe/article/details/78128521

## 开发规约
基于复⽤度分包，总是⼀起使⽤的放在同⼀包下，将接⼝和基类分成独⽴模块，⼤的实现也使⽤
独⽴模块。
所有接⼝都放在模块的根包下，基类放在 support ⼦包下，不同实现⽤放在以扩展点名字命名的
⼦包下。

## 功能列表

- 基于Netty实现长连接式的RPC，包括心跳保持、断线重连、解决粘包半包等
- 基于Zookeeper实现分布式服务注册与发现，并实现了轮询、随机、加权随机、一致性哈希等负载均衡
  算法，以及FailOver、FailFast、FailSafe等多种集群容错方式
- 参考Dubbo实现了分层结构，如
  config,proxy,cluster,protocol,filter,invocation,registry,transport,executor,serialize等层
- 实现了同步、异步、回调、Oneway等多种调用方式
- 实现了TCP、HTTP、InJvm等多种协议
- 实现了客户端侧的Filter，并基于此实现了LeastActive负载均衡算法
- 实现了简易扩展点，泛化调用等功能
- 基于动态代理实现透明RPC，并为其编写了Spring Boot Starter

应该主流RPC框架提供的大部分功能都有了，未来可能会再完善的点有下面这些：

- 优雅停机
- 服务限流、熔断
- 结果缓存
- 多版本，分组
- Router
- 网络层分层、
- Monitor，持久化配置

## 整体大图
### 初始化
![这里写图片描述](http://markdown-1252651195.cossh.myqcloud.com/%E5%88%9D%E5%A7%8B%E5%8C%96.jpg
)
### 服务调用 Consumer Side
![这里写图片描述](http://markdown-1252651195.cossh.myqcloud.com/Consumer.jpg)
## 服务调用 Provider Side
![这里写图片描述](http://markdown-1252651195.cossh.myqcloud.com/Provider.jpg)

### 一次同步调用的时序图
![这里写图片描述](http://markdown-1252651195.cossh.myqcloud.com/Service%20Invocation.png)


## 各层介绍

扩展点：用户可以为某个接口添加自己的实现，在不改变框架源码的前提下，对部分实现进行定制。

最常见的是Filter扩展点。

### config

配置层。

设计准则是Instance wrapped by config，一个配置类中持有了它相关的配置的实例。

对应的核心类：

- ReferenceConfig（对应一个服务接口的引用，持有接口代理实例）
- ServiceConfig（对应一个服务接口的暴露，持有接口实现类实例）
- GlobalConfig（全局配置）
  - ApplicationConfig（应用配置，持有ProxyFactory实例、Serializer实例）
  - RegistryConfig（注册中心配置，持有ServiceRegistry实例）
  - ProtocolConfig（协议配置，持有Protocol实例、Executor实例）
  - ClusterConfig（集群配置，持有LoadBalancer实例、FaultToleranceHandler实例）

### proxy

代理层，主要是为ReferenceConfig生成接口的代理实例（抽象为Invoker，底层是RPC），以及为ServiceConfig生成接口的代理实例（抽象为Invoker，底层直接委托给实现类实例）。

对应的核心类：

- RPCProxyFactory（扩展点，目前有Jdk一种实现）

### registry

注册中心层，主要是服务注册与服务发现，比如对于provider而言，在服务暴露的时候将自己的地址写入到注册中心；对于consumer而言，在服务发现的时候获取服务器的地址，并建立连接，并订阅地址列表，当服务器上下线时，consumer可以感知。

对应的核心类：

- ServiceRegistry（扩展点，目前有Zookeeper一种实现）

### cluster

集群层，主要是将一个接口的集群实现对外暴露为单个实现，屏蔽集群的细节。在集群内部主要是做负载均衡以及集群容错。

对应的核心类：

- LoadBalancer（扩展点，必须继承自AbstractLoadBalancer，目前有随机、加权随机、轮询、一致性哈希、最小活跃度五种实现）
- FaultToleranceHandler（扩展点，目前有FailOver、FailFast、FailSafe三种实现）

### protocol、invocaiton、filter

协议层，也是最核心的一层。

对应的核心类：

- Protocol（扩展点，目前有TCP、HTTP、InJvm三种实现，需要实现响应的Invoker）
- Filter（扩展点，目前有一个为了实现LeastActive算法的ActiveLimitFilter实现）
- Invocation（扩展点，一般不需要扩展，目前有同步、异步、Oneway、Callback四种实现）

### transport

通信层，需要配合协议层，自定义协议实现需要相应的自定义通信实现。

对应的核心类：

- Server（在协议export时如果需要，需要启动服务器）
- Client（在协议refer时如果需要，需要启动客户端）
  它们不是扩展点，只是Protocol在执行相应操作时依赖的组件。

### executor、serialize

其他的较为独立的组件，如任务执行器和序列化器。

对应的核心类：

- TaskExecutor（扩展点，服务器的接口的调用任务线程池，或者客户端的callback线程池，目前有线程池和Disruptor两种实现）
- Serialzer（扩展点，目前有Jdk和Protostuff两种实现）

## 如何扩展一个扩展点

- Filter
  - 在resources目录下创建一个toy目录，然后创建一个名为com.sinjinsong.toy.filter.Filter的文件，文件内容是k=v的格式，k是一个实现类的别名，v是实现类的全类名，比如：bizlog=com.sinjinsong.toy.sample.spring.client.filter.BizLogFilter。这个和Dubbo的实现非常像。
- Serializer
  - 同上，文件名为com.sinjinsong.toy.serialize.api.Serializer，文件内容也是k=v的格式。另外，需要修改application.properties中的rpc.application.serialize={你在文件里写的k}。
