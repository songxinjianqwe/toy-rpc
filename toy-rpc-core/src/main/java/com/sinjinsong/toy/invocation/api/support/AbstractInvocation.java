package com.sinjinsong.toy.invocation.api.support;


import com.sinjinsong.toy.common.domain.RPCRequest;
import com.sinjinsong.toy.common.domain.RPCResponse;
import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.common.util.InvokeParamUtil;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.invocation.api.Invocation;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
public abstract class AbstractInvocation implements Invocation {
    
    @Override
    public final RPCResponse invoke(InvokeParam invokeParam, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws RPCException {
        RPCResponse response;
        ReferenceConfig referenceConfig = InvokeParamUtil.extractReferenceConfigFromInvokeParam(invokeParam);
        RPCRequest rpcRequest = InvokeParamUtil.extractRequestFromInvokeParam(invokeParam);
        try {
            response = doInvoke(rpcRequest, referenceConfig,requestProcessor);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RPCException(e, ErrorEnum.TRANSPORT_FAILURE, "transport异常");
        }
        return response;
    }

    /**
     * 执行对应子类的调用逻辑，可以抛出任何异常
     *
     * @return
     * @throws Throwable
     */
    protected abstract RPCResponse doInvoke(RPCRequest rpcRequest, ReferenceConfig referenceConfig,Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws Throwable;
}
