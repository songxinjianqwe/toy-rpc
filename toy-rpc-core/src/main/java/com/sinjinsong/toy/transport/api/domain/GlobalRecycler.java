package com.sinjinsong.toy.transport.api.domain;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import io.netty.util.Recycler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sinjinsong
 * @date 2018/8/23
 */
public class GlobalRecycler {
    private static Map<Class<?>, Recycler<?>> RECYCLER = new HashMap<>();

    static {
        RECYCLER.put(RPCRequest.class, new Recycler<RPCRequest>() {
            @Override
            protected RPCRequest newObject(Handle<RPCRequest> handle) {
                return new RPCRequest(handle);
            }
        });
        RECYCLER.put(RPCResponse.class, new Recycler<RPCResponse>() {
            @Override
            protected RPCResponse newObject(Handle<RPCResponse> handle) {
                return new RPCResponse(handle);
            }
        });
    }
    

    public static boolean isReusable(Class<?> cls) {
        return RECYCLER.containsKey(cls);
    }
    
    public static <T> T reuse(Class<T> cls) {
        if (isReusable(cls)) {
            return (T) RECYCLER.get(cls).get();
        }
        throw new RPCException(ErrorEnum.RECYCLER_ERROR,"该类型对象不可复用:{}",cls);
    }
}
