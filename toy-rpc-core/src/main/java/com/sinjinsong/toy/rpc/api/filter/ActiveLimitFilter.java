package com.sinjinsong.toy.rpc.api.filter;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.rpc.api.*;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public class ActiveLimitFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RPCException {
//        Result result;
//        try {
//            RPCStatus.incCount(invoker.getUrl());
//            result = invoker.invoke(invocation);
//        }catch(RPCException e) {
//            
//        }
        return null;
    }
}
