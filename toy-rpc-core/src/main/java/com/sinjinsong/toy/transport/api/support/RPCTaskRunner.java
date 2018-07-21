package com.sinjinsong.toy.transport.api.support;

import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.transport.api.converter.MessageConverter;
import com.sinjinsong.toy.transport.api.domain.Message;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by SinjinSong on 2017/7/31.
 */
@Slf4j
public class RPCTaskRunner implements Runnable {
    private ChannelHandlerContext ctx;
    private RPCRequest request;
    private ServiceConfig serviceConfig;
    private MessageConverter messageConverter;

    public RPCTaskRunner(ChannelHandlerContext ctx, RPCRequest request, ServiceConfig serviceConfig, MessageConverter messageConverter) {
        this.ctx = ctx;
        this.request = request;
        this.serviceConfig = serviceConfig;
        this.messageConverter = messageConverter;
    }

    @Override
    public void run() {
        // callback的会以代理的方式调用
        if (serviceConfig.isCallback()) {
            try {
                handle(request);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return;
        }
        RPCResponse response = new RPCResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Throwable t) {
            t.printStackTrace();
            response.setCause(t);
        }
        log.info("已调用完毕服务，结果为: {}", response);
        if (!serviceConfig.isCallbackInterface()) {
            // 如果自己这个接口是一个回调接口，则无需响应
            Object data = messageConverter.convert2Object(Message.buildResponse(response));
            log.info("转换后的消息体为:{}", data);
            // 这里调用ctx的write方法并不是同步的，也是异步的，将该写入操作放入到pipeline中
            ctx.writeAndFlush(data);
        }
    }

    /**
     * 反射调用方法
     *
     * @param request
     * @return
     * @throws Throwable
     */
    private Object handle(RPCRequest request) throws Throwable {
        Object serviceBean = serviceConfig.getRef();

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);

        // 针对callback参数，要将其设置为代理对象
        if (serviceConfig.isCallback()) {
            Class<?> interfaceClass = parameterTypes[serviceConfig.getCallbackParamIndex()];
            parameters[serviceConfig.getCallbackParamIndex()] = Proxy.newProxyInstance(
                    interfaceClass.getClassLoader(),
                    new Class<?>[]{interfaceClass},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getName().equals(serviceConfig.getCallbackMethod())) {
                                // 创建并初始化 RPC 请求
                                RPCRequest callbackRequest = new RPCRequest();
                                log.info("调用callback：{} {}", method.getDeclaringClass().getName(), method.getName());
                                log.info("requestId: {}", request.getRequestId());
                                // 这里requestId是一样的
                                callbackRequest.setRequestId(request.getRequestId());
                                callbackRequest.setInterfaceName(method.getDeclaringClass().getName());
                                callbackRequest.setMethodName(method.getName());
                                callbackRequest.setParameterTypes(method.getParameterTypes());
                                callbackRequest.setParameters(args);
                                // 发起callback请求
                                Object data = messageConverter.convert2Object(Message.buildRequest(callbackRequest));
                                ctx.writeAndFlush(data);
                                return null;
                            } else {
                                return method.invoke(proxy, args);
                            }
                        }
                    }
            );
        }
        return method.invoke(serviceBean, parameters);
    }
}
