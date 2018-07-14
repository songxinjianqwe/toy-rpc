package com.sinjinsong.toy.protocol.api;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public interface Result {
      /**
     * Get invoke result.
     *
     * @return result. if no result return null.
     */
    Object getValue();

    /**
     * Get exception.
     *
     * @return exception. if no exception return null.
     */
    Throwable getException();

    /**
     * Has exception.
     *
     * @return has exception.
     */
    boolean hasException();
}
