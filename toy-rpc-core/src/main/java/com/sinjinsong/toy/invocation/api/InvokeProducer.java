package com.sinjinsong.toy.invocation.api;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/17
 */
@FunctionalInterface
public interface InvokeProducer {
    RPCResponse invoke() throws RPCException;
}
