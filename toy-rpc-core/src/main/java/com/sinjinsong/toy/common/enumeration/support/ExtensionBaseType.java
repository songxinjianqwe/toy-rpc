package com.sinjinsong.toy.common.enumeration.support;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */
public interface ExtensionBaseType<T> {
    T getInstance();

    static ExtensionBaseType valueOf(Class enumType, String s) {
        Enum anEnum = Enum.valueOf(enumType, s);
        if (anEnum instanceof ExtensionBaseType) {
            return (ExtensionBaseType) anEnum;
        } else {
            throw new RPCException(ErrorEnum.VALUE_OF_MUST_BE_APPLIED_TO_EXTENSION_BASE_TYPE, "调用valueOf的枚举{} 必须实现ExtensionBaseTyp接口", enumType);
        }
    }
}
