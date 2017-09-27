package cn.sinjinsong.rpc.core.client;

import cn.sinjinsong.rpc.core.domain.RPCRequest;
import cn.sinjinsong.rpc.core.domain.RPCResponse;
import cn.sinjinsong.rpc.core.domain.RPCResponseFuture;
import cn.sinjinsong.rpc.core.enumeration.MessageType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Slf4j
public class RPCProxy {
    private RPCClient client; // 初始化 RPC 客户端

    public RPCProxy(String registryAddress) {
        client = new RPCClient(registryAddress);
        client.run();
    }

    public void close() {
        client.close();
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RPCRequest request = new RPCRequest(); // 创建并初始化 RPC 请求
                        log.info("调用远程服务：{} {}", method.getDeclaringClass().getName(), method.getName());
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        request.setType(MessageType.NORMAL);
                        RPCResponseFuture responseFuture = client.execute(request); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
                        RPCResponse response = responseFuture.getResponse();
                        log.info("客户端读到响应");
                        if (response.hasError()) {
                            throw response.getCause();
                        } else {
                            return response.getResult();
                        }
                    }
                }
        );
    }
}
