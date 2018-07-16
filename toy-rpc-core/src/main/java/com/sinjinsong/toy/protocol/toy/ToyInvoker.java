package com.sinjinsong.toy.protocol.toy;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.invocation.api.Invocation;
import com.sinjinsong.toy.invocation.async.AsyncInvocation;
import com.sinjinsong.toy.invocation.callback.CallbackInvocation;
import com.sinjinsong.toy.invocation.oneway.OneWayInvocation;
import com.sinjinsong.toy.invocation.sync.SyncInvocation;
import com.sinjinsong.toy.protocol.api.support.AbstractInvoker;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/14
 * 抽象的是一个服务接口的一个服务器地址
 */
@Slf4j
public class ToyInvoker<T> extends AbstractInvoker<T> {
    
    @Override
    protected RPCResponse doInvoke(RPCRequest rpcRequest,ReferenceConfig referenceConfig) throws RPCException {
        // 根据request决定用哪个invocation
        Invocation invocation;
        if (referenceConfig.isAsync()) {
            log.info("async...");
            invocation = new AsyncInvocation();
        } else if (referenceConfig.isCallback()) {
            log.info("callback...");
            invocation = new CallbackInvocation();
        } else if (referenceConfig.isOneWay()) {
            log.info("oneway...");
            invocation = new OneWayInvocation();
        } else {
            log.info("sync...");
            invocation = new SyncInvocation();
        }
        invocation.setInvoker(this);
        invocation.setReferenceConfig(referenceConfig);
        return invocation.invoke(rpcRequest);
    }
}
