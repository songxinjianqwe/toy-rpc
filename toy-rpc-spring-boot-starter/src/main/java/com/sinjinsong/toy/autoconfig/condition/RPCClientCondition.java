package com.sinjinsong.toy.autoconfig.condition;

import com.sinjinsong.toy.config.annotation.RPCReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.reflect.Field;

/**
 * @author sinjinsong
 * @date 2018/7/11
 */
@Slf4j
public class RPCClientCondition implements Condition {
    
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        for (String beanName : context.getRegistry().getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = context.getRegistry().getBeanDefinition(beanName);
            if (beanDefinition.getBeanClassName() == null) {
                continue;
            }
            Class<?> beanClass = null;
            try {
                beanClass = Class.forName(beanDefinition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                RPCReference reference = field.getAnnotation(RPCReference.class);
                if (reference != null) {
                    log.info("RPCServerCondition# needClient? true");
                    return true;
                }
            }
        }
        log.info("RPCServerCondition# needClient? false");
        return false;
    }
}
