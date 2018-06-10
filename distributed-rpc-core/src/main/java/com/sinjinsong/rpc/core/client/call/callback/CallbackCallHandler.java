package com.sinjinsong.rpc.core.client.call.callback;

import com.sinjinsong.rpc.core.annotation.RPCReference;
import com.sinjinsong.rpc.core.client.RPCClient;
import com.sinjinsong.rpc.core.client.call.CallHandler;
import com.sinjinsong.rpc.core.client.context.RPCThreadSharedContext;
import com.sinjinsong.rpc.core.domain.RPCRequest;

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
public class CallbackCallHandler extends CallHandler {

    public CallbackCallHandler(RPCClient rpcClient) {
        super(rpcClient);
    }
    
    @Override
    public Object handleCall(RPCRequest request, RPCReference rpcReference) throws Throwable {
        Object callbackInstance = request.getParameters()[rpcReference.callbackParamIndex()];
        // 该实例无需序列化
        request.getParameters()[rpcReference.callbackParamIndex()] = null;
        RPCThreadSharedContext.registerHandler(generateCallbackHandlerKey(request, rpcReference), callbackInstance);
        rpcClient.execute(request);
        return null;
    }
    
    public static String generateCallbackHandlerKey(RPCRequest request) {
        return new StringBuilder(request.getRequestId()).append(".").append(request.getClassName()).toString();
    }

    private static String generateCallbackHandlerKey(RPCRequest request, RPCReference reference) {
        return new StringBuilder(request.getRequestId()).append(".").append(request.getParameterTypes()[reference.callbackParamIndex()].getName()).toString();
    }
}
