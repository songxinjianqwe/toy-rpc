package com.sinjinsong.toy.common.enumeration;

import com.sinjinsong.toy.common.util.InvokeParamUtil;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.invocation.api.Invocation;
import com.sinjinsong.toy.invocation.async.AsyncInvocation;
import com.sinjinsong.toy.invocation.callback.CallbackInvocation;
import com.sinjinsong.toy.invocation.oneway.OneWayInvocation;
import com.sinjinsong.toy.invocation.sync.SyncInvocation;
import com.sinjinsong.toy.protocol.api.InvokeParam;

/**
 * @author sinjinsong
 * @date 2018/7/15
 */
public enum InvocationType {
    ONEWAY(new OneWayInvocation()),SYNC(new SyncInvocation()),ASYNC(new AsyncInvocation()),CALLBACK(new CallbackInvocation());
    private Invocation invocation;

    InvocationType(Invocation invocation) {
        this.invocation = invocation;
    }
    
    public static Invocation get(InvokeParam invokeParam) {
        ReferenceConfig referenceConfig = InvokeParamUtil.extractReferenceConfigFromInvokeParam(invokeParam);
        if (referenceConfig.isAsync()) {
            return ASYNC.invocation;
        } else if (referenceConfig.isCallback()) {
            return CALLBACK.invocation;
        } else if (referenceConfig.isOneWay()) {
           return ONEWAY.invocation;
        } else {
            return SYNC.invocation;
        }
    }
}
