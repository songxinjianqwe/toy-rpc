package com.sinjinsong.toy.filter.impl;

import com.sinjinsong.toy.common.context.RPCStatus;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.common.util.InvocationUtil;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.invoke.api.Invocation;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
@Slf4j
public class ActiveLimitFilter implements Filter {
    
    @Override
    public RPCResponse invoke(Invocation invocation) throws RPCException {
        RPCResponse result = null;
        String address = InvocationUtil.extractAddressFromInvocation(invocation);
        try {
            log.info("starting,incCount...");
            RPCStatus.incCount(invocation.getInterfaceName(),invocation.getMethodName(),address);
            result = invocation.invoke();
        }catch(RPCException e) {
            log.info("catch exception,decCount...");
            RPCStatus.decCount(invocation.getInterfaceName(),invocation.getMethodName(),address);
            throw e;
        }
        log.info("finished,decCount...");
        RPCStatus.decCount(invocation.getInterfaceName(),invocation.getMethodName(),address);
        return result;
    }
}
