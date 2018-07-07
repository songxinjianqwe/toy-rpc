package com.sinjinsong.toy.rpc.api;


import com.sinjinsong.toy.common.exception.RPCException;

import java.net.URL;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Invoker<T> {
     /**
     * get service interface.
     *
     * @return service interface.
     */
    Class<T> getInterface();

    /**
     * invoke.
     *
     * @param invocation
     * @return result
     * @throws RPCException
     */
    Result invoke(Invocation invocation) throws RPCException;
     
    /**
     * get url.
     *
     * @return url.
     */
    URL getUrl();

    /**
     * is available.
     *
     * @return available.
     */
    boolean isAvailable();

    /**
     * destroy.
     */
    void destroy();
}
