package com.sinjinsong.toy.serialize.api;

import com.sinjinsong.toy.common.exception.RPCException;

/**
 * @author sinjinsong
 * @date 2018/7/12
 */
public interface Serializer {
    <T> byte[] serialize(T obj) throws RPCException;
    <T> T deserialize(byte[] data, Class<T> cls) throws RPCException;
}
