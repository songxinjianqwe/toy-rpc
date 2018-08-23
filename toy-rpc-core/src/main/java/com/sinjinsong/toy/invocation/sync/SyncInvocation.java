package com.sinjinsong.toy.invocation.sync;

import com.sinjinsong.toy.invocation.api.support.AbstractInvocation;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
public class SyncInvocation extends AbstractInvocation {
    
    @Override
    protected RPCResponse doInvoke() throws Throwable {
        Future<RPCResponse> future = doCustomProcess();
        RPCResponse response = future.get(getReferenceConfig().getTimeout(), TimeUnit.MILLISECONDS);
        log.info("客户端读到响应:{}", response);
        return response;
    }
}
