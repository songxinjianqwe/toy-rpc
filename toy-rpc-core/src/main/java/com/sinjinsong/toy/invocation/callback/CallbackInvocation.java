package com.sinjinsong.toy.invocation.callback;


import com.sinjinsong.toy.common.context.RPCThreadSharedContext;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.invocation.api.support.AbstractInvocation;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/6/10
 * 客户端会给出callback接口的接口名、方法名
 * 服务端会给出callback接口的接口名、方法名以及callback参数在service方法中参数的索引
 * 在客户端发出请求前，会将该回调方法暴露出来，就像服务端一样。
 * 在服务端接收到请求后，会先执行service方法，当拿到结果后，RPC请求客户端的进行回调方法调用。
 * 虽然方法是在客户端被调用的，但占用了服务端的CPU，是在服务端的线程中完成的。
 * 简言之就是客户端RPC服务器，服务器RPC客户端。
 * 这里约定，客户端rpc服务器，服务器不会影响该request；服务器转而会rpc服务器，两个request的id是一样的。
 * 通过这个相同的requestid来定位callback实例
 */
public abstract class CallbackInvocation extends AbstractInvocation {
    
    @Override
    protected RPCResponse doInvoke() throws Throwable {
        RPCRequest rpcRequest = getRpcRequest();
        ReferenceConfig referenceConfig = getReferenceConfig();
        Object callbackInstance = rpcRequest.getParameters()[referenceConfig.getCallbackParamIndex()];
        // 该实例无需序列化
        rpcRequest.getParameters()[referenceConfig.getCallbackParamIndex()] = null;

        registerCallbackHandler(rpcRequest, callbackInstance);
        getResponseFuture();
        return null;
    }
    
    private void registerCallbackHandler(RPCRequest request, Object callbackInstance) {
        Class<?> interfaceClass = callbackInstance.getClass().getInterfaces()[0];

        ServiceConfig config = ServiceConfig.builder()
                .interfaceName(interfaceClass.getName())
                .interfaceClass((Class<Object>) interfaceClass)
                .isCallbackInterface(true)
                .ref(callbackInstance).build();
        RPCThreadSharedContext.registerHandler(generateCallbackHandlerKey(request, getReferenceConfig()),
                config);

    }

    public static String generateCallbackHandlerKey(RPCRequest request) {
        return new StringBuilder(request.getRequestId()).append(".").append(request.getInterfaceName()).toString();
    }

    private static String generateCallbackHandlerKey(RPCRequest request, ReferenceConfig referenceConfig) {
        return new StringBuilder(request.getRequestId()).append(".").append(request.getParameterTypes()[referenceConfig.getCallbackParamIndex()].getName()).toString();
    }
}
