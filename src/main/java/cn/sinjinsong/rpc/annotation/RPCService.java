package cn.sinjinsong.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by SinjinSong on 2017/7/31.
 */
@Target(ElementType.TYPE)  
@Retention(RetentionPolicy.RUNTIME) 
public @interface RPCService {
    Class<?> value();
}
