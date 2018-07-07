package com.sinjinsong.toy.proxy;

/**
 * @author sinjinsong
 * @date 2018/3/11
 */

import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.config.annotation.RPCReference;
import com.sinjinsong.toy.exchange.async.AsyncExchangeHandler;
import com.sinjinsong.toy.exchange.callback.CallbackExchangeHandler;
import com.sinjinsong.toy.exchange.sync.SyncExchangeHandler;
import com.sinjinsong.toy.transport.client.RPCClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Set;


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
public class RPCConsumerProxyFactoryBeanRegistry implements BeanDefinitionRegistryPostProcessor {
    private String basePackage;
    private RPCClient client;

    public RPCConsumerProxyFactoryBeanRegistry(RPCClient client, String basePackage) {
        this.client = client;
        this.basePackage = basePackage;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        log.info("正在添加动态代理类的FactoryBean");
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);

        for (BeanDefinition beanDefinition : candidateComponents) {
            log.info("beanDefinition: {}", beanDefinition);
            log.info("{}", beanDefinition.getBeanClassName());
            String beanClassName = beanDefinition.getBeanClassName();
            Class<?> beanClass = null;
            try {
                beanClass = Class.forName(beanClassName);
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
                    ReferenceConfig config = ReferenceConfig.builder()
                            .interfaceName(className.getName())
                            .interfaceClass((Class<Object>) className)
                            .isAsync(reference.async())
                            .isCallback(reference.callback())
                            .timeout(reference.timeout())
                            .callbackMethod(reference.callbackMethod())
                            .callbackParamIndex(reference.callbackParamIndex())
                            .build();
                    log.info("创建了对应的动态代理,reference {}", reference);
                    BeanDefinitionHolder holder = createBeanDefinition(className.getName(), config);
                    BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
                }
            }
        }
    }

    private BeanDefinitionHolder createBeanDefinition(String className, ReferenceConfig config) {
        log.info("Creating bean definition for class: {}", className);
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(RPCConsumerProxyFactoryBean.class);
        String beanName = StringUtils.uncapitalize(className.substring(className.lastIndexOf('.') + 1));
        definition.addPropertyValue("interfaceClass", className);
        definition.addPropertyValue("client", client);
        if (config.isAsync()) {
            definition.addPropertyValue("exchangeHandler", new AsyncExchangeHandler(client));
        } else if (config.isCallback()) {
            definition.addPropertyValue("exchangeHandler", new CallbackExchangeHandler(client));
        } else {
            definition.addPropertyValue("exchangeHandler", new SyncExchangeHandler(client));
        }
        definition.addPropertyValue("referenceConfig", config);
        return new BeanDefinitionHolder(definition.getBeanDefinition(), beanName);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

}

