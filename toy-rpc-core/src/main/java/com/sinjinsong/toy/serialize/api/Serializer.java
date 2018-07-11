package com.sinjinsong.toy.serialize.api;

/**
 * @author sinjinsong
 * @date 2018/7/12
 */
public interface Serializer {
    <T> byte[] serialize(T obj);
    <T> T deserialize(byte[] data, Class<T> cls);
}
