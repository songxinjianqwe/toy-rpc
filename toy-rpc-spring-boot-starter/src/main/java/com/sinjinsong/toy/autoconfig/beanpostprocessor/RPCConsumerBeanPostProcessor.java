package com.sinjinsong.toy.autoconfig.beanpostprocessor;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.config.annotation.RPCReference;
import com.sinjinsong.toy.filter.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;

import java.lang.reflect.Field;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
@Slf4j
public class RPCConsumerBeanPostProcessor extends AbstractRPCBeanPostProcessor{
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            Class<?> interfaceClass = field.getType();
            RPCReference reference = field.getAnnotation(RPCReference.class);
            if (reference != null) {
                ReferenceConfig config = ReferenceConfig.createReferenceConfig(
                        interfaceClass,
                        reference.async(),
                        reference.callback(),
                        reference.oneway(),
                        reference.timeout(),
                        reference.callbackMethod(),
                        reference.callbackParamIndex(),
                        ctx.getBeansOfType(Filter.class).values()
                );
                initConfig(config);
                try {
                    field.set(bean,config.get());
                } catch (IllegalAccessException e) {
                    throw new RPCException("set proxy failed",e);
                }
                log.info("注入依赖:{}",interfaceClass);
            }
        }
        return bean;
    }
}
