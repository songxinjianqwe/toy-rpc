package com.sinjinsong.rpc.core.proxy;

import com.sinjinsong.rpc.core.annotation.RPCReference;
import com.sinjinsong.rpc.core.client.RPCClient;
import com.sinjinsong.rpc.core.domain.RPCRequest;
import com.sinjinsong.rpc.core.domain.RPCResponse;
import com.sinjinsong.rpc.core.domain.RPCResponseFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.UUID;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@Slf4j
public class RPCProxyFactory{
    
    public static void init(ApplicationContext ctx,RPCClient client,String basePackage) {
        log.info("RPCProxyFactory init...");
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        Set<BeanDefinition> beans = scanner.findCandidateComponents(basePackage);

        log.info("beans size:{}", beans.size());
        try {
            for (BeanDefinition beanDefinition : beans) {
                Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
                Object bean = ctx.getBean(beanClass);

                log.info("{}", beanDefinition);
                log.info("{}", beanDefinition.getBeanClassName());
                if (!beanDefinition.getBeanClassName().startsWith(basePackage)) {
                    continue;
                }
                Field[] fields = beanClass.getDeclaredFields();
                for (Field field : fields) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    RPCReference reference = field.getAnnotation(RPCReference.class);
                    if (reference != null) {
                        log.info("创建了对应的动态代理");
                        Object value = RPCProxyFactory.getRemoteProxyObject(field.getType(), client); //动态代理
                        field.set(bean, value);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Object getRemoteProxyObject(Class<?> interfaceClass, RPCClient client) {
        return Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 创建并初始化 RPC 请求
                        RPCRequest request = new RPCRequest();
                        log.info("调用远程服务：{} {}", method.getDeclaringClass().getName(), method.getName());
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
                        RPCResponseFuture responseFuture = client.execute(request);
                        RPCResponse response = responseFuture.getResponse();
                        log.info("客户端读到响应");
                        if (response.hasError()) {
                            throw response.getCause();
                        } else {
                            return response.getResult();
                        }
                    }
                }
        );
    }
}
