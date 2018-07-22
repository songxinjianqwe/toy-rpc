package com.sinjinsong.toy.common.exception;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.util.PlaceHolderReplacementUtil;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public class RPCException extends RuntimeException {
    private ErrorEnum errorEnum;

    public RPCException(ErrorEnum errorEnum, String message, Object... args) {
        super(PlaceHolderReplacementUtil.replace(message, args));
        this.errorEnum = errorEnum;
    }

    public RPCException(ErrorEnum errorEnum, Throwable cause, String message, Object... args) {
        super(PlaceHolderReplacementUtil.replace(message, args), cause);
        this.errorEnum = errorEnum;
    }

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }
}
