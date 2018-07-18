package com.sinjinsong.toy.filter.impl;

import com.sinjinsong.toy.common.context.RPCStatus;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
@Slf4j
public class ActiveLimitFilter implements Filter {

    @Override
    public RPCResponse invoke(Invoker invoker, InvokeParam invokerParam) throws RPCException {
        RPCResponse result = null;
        try {
            log.info("starting,incCount...,{}",invokerParam);
            RPCStatus.incCount(invokerParam.getInterfaceName(), invokerParam.getMethodName(), invoker.getAddress());
            result = invoker.invoke(invokerParam);
        } catch (RPCException e) {
            log.info("catch exception,decCount...,{}",invokerParam);
            RPCStatus.decCount(invokerParam.getInterfaceName(), invokerParam.getMethodName(), invoker.getAddress());
            throw e;
        }
        log.info("finished,decCount...,{}",invokerParam);
        RPCStatus.decCount(invokerParam.getInterfaceName(), invokerParam.getMethodName(), invoker.getAddress());
        return result;
    }
}
