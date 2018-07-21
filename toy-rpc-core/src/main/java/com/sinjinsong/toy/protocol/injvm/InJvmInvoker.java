package com.sinjinsong.toy.protocol.injvm;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Protocol;
import com.sinjinsong.toy.protocol.api.support.AbstractInvoker;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;

import java.lang.reflect.Method;

/**
 * @author sinjinsong
 * @date 2018/7/18
 */
public class InJvmInvoker<T> extends AbstractInvoker<T> {
    private Protocol protocol;
    
    @Override
    public RPCResponse invoke(InvokeParam invokeParam) throws RPCException {
        Object serviceBean = protocol.referLocalService(invokeParam.getInterfaceName()).getRef();
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = invokeParam.getMethodName();
        Class<?>[] parameterTypes = invokeParam.getParameterTypes();
        Object[] parameters = invokeParam.getParameters();
        RPCResponse response = new RPCResponse();
        response.setRequestId(invokeParam.getRequestId());
        try {
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(serviceBean, parameters);
            response.setResult(result);
        } catch (Throwable t) {
            t.printStackTrace();
            response.setCause(t);
        }
        return response;
    }
    
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
}
