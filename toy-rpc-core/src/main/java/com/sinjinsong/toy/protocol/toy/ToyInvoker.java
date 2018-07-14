package com.sinjinsong.toy.protocol.toy;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.invocation.api.Invocation;
import com.sinjinsong.toy.invocation.async.AsyncInvocation;
import com.sinjinsong.toy.invocation.callback.CallbackInvocation;
import com.sinjinsong.toy.invocation.oneway.OneWayInvocation;
import com.sinjinsong.toy.invocation.sync.SyncInvocation;
import com.sinjinsong.toy.protocol.api.support.AbstractInvoker;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public class ToyInvoker<T> extends AbstractInvoker<T> {

    @Override
    protected Object doInvoke(RPCRequest rpcRequest) throws RPCException {
        // 根据request决定用哪个invocation
        Invocation invocation;
        if (referenceConfig.isAsync()) {
            invocation = new AsyncInvocation();
        } else if (referenceConfig.isCallback()) {
            invocation = new CallbackInvocation();
        } else if (referenceConfig.isOneWay()) {
            invocation = new OneWayInvocation();
        } else {
            invocation = new SyncInvocation();
        }
        invocation.setReferenceConfig(referenceConfig);
        return invocation.invoke(rpcRequest);
    }
}
