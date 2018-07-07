package com.sinjinsong.toy.config.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by SinjinSong on 2017/7/31.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RPCService {
    boolean callback() default false;
    String callbackMethod() default "";
    int callbackParamIndex() default 1;
}
