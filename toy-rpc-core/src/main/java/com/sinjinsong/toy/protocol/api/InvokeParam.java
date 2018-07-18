package com.sinjinsong.toy.protocol.api;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public interface InvokeParam {
    String getInterfaceName();
    
    String getMethodName();

    Class<?>[] getParameterTypes();

    Object[] getParameters();
    
    String getRequestId();
}
