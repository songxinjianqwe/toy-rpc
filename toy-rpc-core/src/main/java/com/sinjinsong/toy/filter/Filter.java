package com.sinjinsong.toy.filter;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.invoke.api.Invocation;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Filter {
    RPCResponse invoke(Invocation invocation) throws RPCException;
}
