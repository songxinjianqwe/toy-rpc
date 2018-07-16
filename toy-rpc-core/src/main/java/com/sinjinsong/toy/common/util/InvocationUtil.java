package com.sinjinsong.toy.common.util;

import com.sinjinsong.toy.invoke.api.Invocation;
import com.sinjinsong.toy.invoke.api.support.AbstractInvocation;
import com.sinjinsong.toy.invoke.api.support.InvocationDelegate;
import com.sinjinsong.toy.protocol.api.Invoker;

/**
 * @author sinjinsong
 * @date 2018/7/17
 */
public class InvocationUtil {
    public static String extractAddressFromInvocation(Invocation invocation) {
        if (invocation instanceof AbstractInvocation) {
            return AbstractInvocation.class.cast(invocation).getInvoker().getEndpoint().getAddress();
        }
        return null;
    }

    public static void setInvoker(Invocation invocation, Invoker invoker) {
        if (invocation instanceof AbstractInvocation) {
            AbstractInvocation.class.cast(invocation).setInvoker(invoker);
        }
    }

    public static Invocation extractOriginalInvocation(Invocation invocation) {
        if (invocation instanceof InvocationDelegate) {
            InvocationDelegate delegate = (InvocationDelegate) invocation;
            return delegate.getDelegate();
        } else {
            return invocation;
        }
    }
}
