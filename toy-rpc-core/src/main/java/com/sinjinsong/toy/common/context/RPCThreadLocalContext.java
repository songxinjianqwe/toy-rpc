package com.sinjinsong.toy.common.context;

import com.sinjinsong.toy.protocol.api.Invoker;

import java.util.concurrent.Future;

/**
 * @author sinjinsong
 * @date 2018/6/10
 * 线程私有！
 */
public class RPCThreadLocalContext {
    private static final ThreadLocal<RPCThreadLocalContext> RPC_CONTEXT = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return new RPCThreadLocalContext();
        }
    };

    private RPCThreadLocalContext() {
    }

    private Future future;
    private Invoker invoker;


    public static RPCThreadLocalContext getContext() {
        return RPC_CONTEXT.get();
    }

    public Future getFuture() {
        return future;
    }

    public Invoker getInvoker() {
        return invoker;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

}
