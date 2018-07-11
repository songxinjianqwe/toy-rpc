package com.sinjinsong.toy.autoconfig.condition;

import com.sinjinsong.toy.config.annotation.RPCService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * @author sinjinsong
 * @date 2018/7/11
 */
@Slf4j
public class RPCServerCondition implements Condition  {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> beans = context.getBeanFactory().getBeansWithAnnotation(RPCService.class);
        log.info("RPCServerCondition# needServer? {}",!beans.isEmpty());  
        return !beans.isEmpty();
    }
}
