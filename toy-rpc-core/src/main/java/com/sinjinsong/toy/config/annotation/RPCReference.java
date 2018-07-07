package com.sinjinsong.toy.config.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Autowired
public @interface RPCReference {
    boolean async() default false;
    boolean callback() default  false;
    long timeout() default 3000;
    String callbackMethod() default "";
    int callbackParamIndex() default 1;
}
