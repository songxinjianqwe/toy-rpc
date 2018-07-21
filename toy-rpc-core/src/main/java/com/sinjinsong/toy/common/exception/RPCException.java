package com.sinjinsong.toy.common.exception;

import com.sinjinsong.toy.common.util.PlaceHolderReplacementUtil;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public class RPCException extends RuntimeException {
    public RPCException(String message, Object... args) {
        super(PlaceHolderReplacementUtil.replace(message,args));
    }

    public RPCException(Throwable cause, String message, Object... args) {
        super(PlaceHolderReplacementUtil.replace(message,args), cause);
    }
}
