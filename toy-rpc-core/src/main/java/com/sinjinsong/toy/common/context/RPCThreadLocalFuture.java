package com.sinjinsong.toy.common.context;

import java.util.concurrent.Future;

/**
 * @author sinjinsong
 * @date 2018/6/10
 * 线程私有！
 */
public class RPCThreadLocalFuture<T> {
    private static final ThreadLocal<RPCThreadLocalFuture> RPC_CONTEXT = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return new RPCThreadLocalFuture();
        }
    };

    private RPCThreadLocalFuture() {
    }

    private Future<T> future;

    public void setFuture(Future<T> future) {
        this.future = future;
    }

    public static RPCThreadLocalFuture getContext() {
        return RPC_CONTEXT.get();
    }

    public Future<T> getFuture() {
        return future;
    }
}
