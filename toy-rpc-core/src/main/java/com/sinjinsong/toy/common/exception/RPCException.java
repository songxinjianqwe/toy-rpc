package com.sinjinsong.toy.common.exception;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.util.PlaceHolderUtil;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
public class RPCException extends RuntimeException {
    private ErrorEnum errorEnum;

    public RPCException(ErrorEnum errorEnum, String message, Object... args) {
        super(PlaceHolderUtil.replace(message, args));
        this.errorEnum = errorEnum;
    }

    public RPCException(Throwable cause, ErrorEnum errorEnum, String message, Object... args) {
        super(PlaceHolderUtil.replace(message, args), cause);
        this.errorEnum = errorEnum;
    }

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }
}
