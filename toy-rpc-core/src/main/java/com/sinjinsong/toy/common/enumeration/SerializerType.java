package com.sinjinsong.toy.common.enumeration;

import com.sinjinsong.toy.common.enumeration.support.ExtensionBaseType;
import com.sinjinsong.toy.serialize.api.Serializer;
import com.sinjinsong.toy.serialize.jdk.JdkSerializer;
import com.sinjinsong.toy.serialize.protostuff.ProtostuffSerializer;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public enum SerializerType implements ExtensionBaseType<Serializer> {
    PROTOSTUFF(new ProtostuffSerializer()),
    JDK(new JdkSerializer());
    
    private Serializer serializer;

    SerializerType(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public Serializer getInstance() {
        return serializer;
    }
}
