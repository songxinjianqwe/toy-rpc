package com.sinjinsong.toy.serialize.json;

import com.alibaba.fastjson.JSONObject;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.serialize.api.Serializer;

/**
 * @author sinjinsong
 * @date 2018/8/23
 */
public class JsonSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) throws RPCException {
        return JSONObject.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) throws RPCException {
        return JSONObject.parseObject(data, cls);
    }
}
