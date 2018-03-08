package com.sinjinsong.rpc.core.server;

import com.sinjinsong.rpc.core.domain.RPCRequest;
import com.sinjinsong.rpc.core.domain.RPCResponse;
import com.sinjinsong.rpc.core.enumeration.MessageType;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by SinjinSong on 2017/7/31.
 */
@Slf4j
public class RequestExecutor implements Runnable {
    private ChannelHandlerContext ctx;
    private RPCRequest request;
    private Map<String, Object> handlerMap;

    public RequestExecutor(ChannelHandlerContext ctx, RPCRequest request, Map<String, Object> handlerMap) {
        this.ctx = ctx;
        this.request = request;
        this.handlerMap = handlerMap;
    }

    @Override
    public void run() {
        
        RPCResponse response = new RPCResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Throwable t) {
            t.printStackTrace();
            response.setCause(t);
        }
        log.info("服务器已调用完毕服务，结果为: {}", response);
        response.setType(MessageType.NORMAL);
        // 这里调用ctx的write方法并不是同步的，也是异步的，将该写入操作放入到pipeline中
        ctx.writeAndFlush(response);
    }

    /**
     * 反射调用方法
     *
     * @param request
     * @return
     * @throws Throwable
     */
    private Object handle(RPCRequest request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }
}
