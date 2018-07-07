package com.sinjinsong.toy.rpc.api;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Exporter<T> {
     Invoker<T> getInvoker();

    /**
     * unexport.
     * <p>
     * <code>
     * getInvoker().destroy();
     * </code>
     */
    void unexport();
}
