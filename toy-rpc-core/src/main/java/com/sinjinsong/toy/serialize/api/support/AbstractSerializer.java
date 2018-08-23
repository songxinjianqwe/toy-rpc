package com.sinjinsong.toy.serialize.api.support;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.common.domain.GlobalRecycler;

/**
 * @author sinjinsong
 * @date 2018/8/23
 */
public abstract class AbstractSerializer implements Serializer {
    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) throws RPCException {
        T t;
        if (GlobalRecycler.isReusable(cls)) {
            t = GlobalRecycler.reuse(cls);
        } else {
            try {
                t = cls.newInstance();
            } catch (Exception e) {
                throw new RPCException(ErrorEnum.SERIALIZER_ERROR, "实例化对象失败", e);
            }
        }
        return deserializeOnObject(data, cls, t);
    }

    protected <T> T deserializeOnObject(byte[] data, Class<T> cls, T t) {
        throw new UnsupportedOperationException();
    }
}
