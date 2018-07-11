package com.sinjinsong.toy.proxy;

import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.config.annotation.RPCReference;
import com.sinjinsong.toy.remoting.exchange.async.AsyncExchangeHandler;
import com.sinjinsong.toy.remoting.exchange.callback.CallbackExchangeHandler;
import com.sinjinsong.toy.remoting.exchange.sync.SyncExchangeHandler;
import com.sinjinsong.toy.remoting.transport.client.RPCClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * @author sinjinsong
 * @date 2018/3/10
 * <p>
 * BeanDefinitionRegistryPostProcessor继承自BeanFactoryPostProcessor，
 * 是一种比较特殊的BeanFactoryPostProcessor。BeanDefinitionRegistryPostProcessor中定义的
 * postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)方法
 * 可以让我们实现自定义的注册bean定义的逻辑。
 * <p>
 * ClassPathScanningCandidateComponentProvider可以根据一定的规则扫描类路径下满足特定条件的Class
 * 来作为候选的bean定义。 ClassPathScanningCandidateComponentProvider在扫描时可以通过TypeFilter
 * 来指定需要匹配的类和需要排除的类，使用ClassPathScanningCandidateComponentProvider时可以通过构造参数
 * useDefaultFilter指定是否需要使用默认的TypeFilter，
 * 默认的TypeFilter将包含类上拥有 @Component、@Service、@Repository、@Controller、
 * @javax.annotation.ManagedBean和@javax.inject.Named注解的类。在扫描时需要指定扫描的根包路径。
 */
@Slf4j
public class RPCProxyFactoryBeanRegistry implements BeanDefinitionRegistryPostProcessor {
    private RPCClient client;
    public static boolean HAS_REFERENCE = false;
    
    public RPCProxyFactoryBeanRegistry(RPCClient client) {
        this.client = client;
    }
    
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        log.info("正在添加动态代理类的FactoryBean");
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if(beanDefinition.getBeanClassName() == null) {
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
                Class<?> className = field.getType();
                RPCReference reference = field.getAnnotation(RPCReference.class);
                if (reference != null) {
                    HAS_REFERENCE = true;
                    ReferenceConfig config = ReferenceConfig.builder()
                            .interfaceName(className.getName())
                            .interfaceClass((Class<Object>) className)
                            .isAsync(reference.async())
                            .isCallback(reference.callback())
                            .timeout(reference.timeout())
                            .callbackMethod(reference.callbackMethod())
                            .callbackParamIndex(reference.callbackParamIndex())
                            .build();
                    createBeanDefinitionIfAbsent(registry, className.getName(), config);
                }
            }
        }
    }

    private void createBeanDefinitionIfAbsent(BeanDefinitionRegistry registry, String className, ReferenceConfig config) {
        log.info("Creating bean definition for class: {}", className);
        String beanName = StringUtils.uncapitalize(className.substring(className.lastIndexOf('.') + 1));
//        if (!registry.containsBeanDefinition(beanName)) {
//            log.info("duplicated beanDefinition! {}",beanName);
//            return;
//        }
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(RPCProxyFactoryBean.class);
        definition.addPropertyValue("interfaceClass", className);
        if (config.isAsync()) {
            definition.addPropertyValue("exchangeHandler", new AsyncExchangeHandler(client));
        } else if (config.isCallback()) {
            definition.addPropertyValue("exchangeHandler", new CallbackExchangeHandler(client));
        } else {
            definition.addPropertyValue("exchangeHandler", new SyncExchangeHandler(client));
        }
        definition.addPropertyValue("referenceConfig", config);
        log.info("创建了对应的动态代理,ReferenceConfig {}", config);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(definition.getBeanDefinition(), beanName);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
    
    
}

