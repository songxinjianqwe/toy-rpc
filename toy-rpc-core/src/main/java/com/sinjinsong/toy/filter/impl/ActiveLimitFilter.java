package com.sinjinsong.toy.filter.impl;

import com.sinjinsong.toy.common.context.RPCStatus;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public class ActiveLimitFilter implements Filter {

    @Override
    public RPCResponse invoke(Invoker<?> invoker,ReferenceConfig referenceConfig,RPCRequest rpcRequest) throws RPCException {
        RPCResponse result = null;
        try {
            RPCStatus.incCount(rpcRequest.getInterfaceName(),rpcRequest.getMethodName(),invoker.getEndpoint().getAddress());
            result = invoker.invoke(rpcRequest,referenceConfig);
        }catch(RPCException e) {
            RPCStatus.decCount(rpcRequest.getInterfaceName(),rpcRequest.getMethodName(),invoker.getEndpoint().getAddress());
            throw e;
        }
        RPCStatus.decCount(rpcRequest.getInterfaceName(),rpcRequest.getMethodName(),invoker.getEndpoint().getAddress());
        return result;
    }
}
