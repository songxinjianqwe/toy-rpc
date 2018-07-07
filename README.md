# 基于Netty、ProtoStuff、Zookeeper实现的分布式RPC框架
## 基于Netty实现长连接式的RPC，包括心跳保持、断线重连等
## 基于Protostuff实现消息的序列化
## 基于Zookeeper实现分布式注册与发现
## 基于Spring实现代理生成与注解式调用


- 可以考虑使用FactoryBean，在Spring初始化前期自定义BeanFactoryPostProcessor，将FactoryBean的BeanDefinition动态放入Registry中，然后Spring会去调用
getObject方法获取实例（MyBatis也是这样做的）。但是前期无法获得所依赖的对象的bean
- 比较丑陋的方法，直接扫描所有的bean，将加了@RPCReference注解的field替换为其代理后的对象
既然我们要像调用本地方法那样调用远程服务， 那么就应该生成代理来隐藏调用远程服务的细节

服务发现与服务注册
负载均衡
客户端通过服务注册中心拿到一堆地址，该调哪个呢？最简单的方式，可以通过RR、WRR的方式去做LB。

如果做得更深入一些，可以从以下角度去优化：

根据服务实例的metrics做出动态调整, 比如响应时间等
利用一致性哈希， 提高本地缓存利用率
服务调用超时与重试： 在调用一个服务实例的时候，如果超时或者报错，怎么处理？
服务限流：如何限制最大并发数？这个又可以从客户端和服务端两个角度分析。
## 开发规约
基于复⽤度分包，总是⼀起使⽤的放在同⼀包下，将接⼝和基类分成独⽴模块，⼤的实现也使⽤
独⽴模块。
所有接⼝都放在模块的根包下，基类放在 support ⼦包下，不同实现⽤放在以扩展点名字命名的
⼦包下。
![image](http://markdown-1252651195.cossh.myqcloud.com/%E6%9C%AA%E5%91%BD%E5%90%8D%E6%96%87%E4%BB%B6%20%281%29.jpg)