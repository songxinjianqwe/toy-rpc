package com.sinjinsong.toy.serialize.hessian;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.serialize.api.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author sinjinsong
 * @date 2018/8/23
 * 
 * 不可用，writeObject时会抛一个ArrayIndexOutOfBoundsException异常
 */
@Slf4j
public class HessianSerializer implements Serializer {
    
    @Override
    public <T> byte[] serialize(T obj) throws RPCException {
        byte[] results = null;
        ByteArrayOutputStream os = null;
        HessianSerializerOutput hessianOutput = null;
        try {
            os = new ByteArrayOutputStream();
            hessianOutput = new HessianSerializerOutput(os);
            hessianOutput.writeObject(obj);
            results = os.toByteArray();
        } catch (Exception e) {
            throw new RPCException(e, ErrorEnum.SERIALIZER_ERROR, "序列化异常:{}", obj);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                log.error("{}", e);
            }
        }
        return results;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) throws RPCException {
        if (data == null) {
            throw new NullPointerException();
        }
        T result = null;
        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(data);
            HessianSerializerInput hessianInput = new HessianSerializerInput(is);
            result = cls.cast(hessianInput.readObject());
        } catch (Exception e) {
            throw new RPCException(e, ErrorEnum.SERIALIZER_ERROR, "反序列化异常:{}", cls);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("{}", e);
            }
        }
        return result;
    }
}
