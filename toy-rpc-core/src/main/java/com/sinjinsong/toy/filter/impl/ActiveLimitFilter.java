package com.sinjinsong.toy.filter.impl;

import com.sinjinsong.toy.common.context.RPCStatus;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.common.util.InvocationUtil;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.invoke.api.Invocation;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public class ActiveLimitFilter implements Filter {
    
    @Override
    public RPCResponse invoke(Invocation invocation) throws RPCException {
        RPCResponse result = null;
        String address = InvocationUtil.extractAddressFromInvocation(invocation);
        try {
            RPCStatus.incCount(invocation.getInterfaceName(),invocation.getMethodName(),address);
            result = invocation.invoke();
        }catch(RPCException e) {
            RPCStatus.decCount(invocation.getInterfaceName(),invocation.getMethodName(),address);
            throw e;
        }
        RPCStatus.decCount(invocation.getInterfaceName(),invocation.getMethodName(),address);
        return result;
    }
}
