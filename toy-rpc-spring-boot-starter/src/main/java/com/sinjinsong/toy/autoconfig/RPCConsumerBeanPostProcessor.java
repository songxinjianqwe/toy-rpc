package com.sinjinsong.toy.autoconfig;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.config.*;
import com.sinjinsong.toy.config.annotation.RPCReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public class RPCConsumerBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext ctx;
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            Class<?> className = field.getType();
            RPCReference reference = field.getAnnotation(RPCReference.class);
            if (reference != null) {
                ReferenceConfig config = ReferenceConfig.getSingletonByInterfaceName(
                        className,
                        reference.async(),
                        reference.callback(),
                        reference.oneway(),
                        reference.timeout(),
                        reference.callbackMethod(),
                        reference.callbackParamIndex()
                );
                config.init(
                        ctx.getBean(ApplicationConfig.class),
                        ctx.getBean(ClusterConfig.class),
                        ctx.getBean(ProtocolConfig.class),
                        ctx.getBean(RegistryConfig.class)
                );
                try {
                    field.set(bean,config.get());
                } catch (IllegalAccessException e) {
                    throw new RPCException("set proxy failed",e);
                }
            }
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
