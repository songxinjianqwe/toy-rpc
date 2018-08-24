package com.sinjinsong.toy.invocation.sync;

import com.sinjinsong.toy.common.domain.RPCRequest;
import com.sinjinsong.toy.common.domain.RPCResponse;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.invocation.api.support.AbstractInvocation;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
public class SyncInvocation extends AbstractInvocation {
    
    @Override
    protected RPCResponse doInvoke(RPCRequest rpcRequest, ReferenceConfig referenceConfig, Function<RPCRequest, Future<RPCResponse>> requestProcessor) throws Throwable {
        Future<RPCResponse> future = requestProcessor.apply(rpcRequest);
        RPCResponse response = future.get(referenceConfig.getTimeout(), TimeUnit.MILLISECONDS);
        log.info("客户端读到响应:{}", response);
        return response;
    }
}
