package com.sinjinsong.toy.common.context;

import java.util.concurrent.Future;

/**
 * @author sinjinsong
 * @date 2018/6/10
 * 线程私有！
 */
public class RPCThreadLocalContext<T> {
    private static final ThreadLocal<RPCThreadLocalContext> RPC_CONTEXT = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return new RPCThreadLocalContext();
        }
    };

    private RPCThreadLocalContext() {
    }

    private Future<T> future;

    public void setFuture(Future<T> future) {
        this.future = future;
    }

    public static RPCThreadLocalContext getContext() {
        return RPC_CONTEXT.get();
    }

    public Future<T> getFuture() {
        return future;
    }
}
