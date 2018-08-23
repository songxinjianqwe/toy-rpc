package com.sinjinsong.toy.invocation.api;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.common.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Invocation {
    RPCResponse invoke() throws RPCException;
   
}
