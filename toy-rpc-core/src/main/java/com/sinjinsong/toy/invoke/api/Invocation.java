package com.sinjinsong.toy.invoke.api;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Invocation {
    RPCResponse invoke() throws RPCException;
    
    String getInterfaceName();
    
    String getMethodName();

    Class<?>[] getParameterTypes();

    Object[] getParameters();
    
    String getRequestId();
}
