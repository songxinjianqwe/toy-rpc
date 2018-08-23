package com.sinjinsong.toy.filter;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Filter {
    RPCResponse invoke(Invoker invoker, InvokeParam invokeParam) throws RPCException;
}
