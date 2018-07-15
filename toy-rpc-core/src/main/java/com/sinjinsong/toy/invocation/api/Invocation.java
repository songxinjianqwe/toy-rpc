package com.sinjinsong.toy.invocation.api;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Invocation {
    RPCResponse invoke(RPCRequest request) throws RPCException;
    void setReferenceConfig(ReferenceConfig config);
}
