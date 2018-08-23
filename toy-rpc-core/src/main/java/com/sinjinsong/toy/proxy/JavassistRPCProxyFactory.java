package com.sinjinsong.toy.proxy;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.proxy.api.support.AbstractRPCProxyFactory;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.Method;

/**
 * @author sinjinsong
 * @date 2018/8/23
 */
public class JavassistRPCProxyFactory extends AbstractRPCProxyFactory {
    @Override
    protected <T> T doCreateProxy(Class<T> interfaceClass, Invoker<T> invoker) {
        //代理对象
        ProxyFactory factory = new ProxyFactory();

        //设定需要代理的类
        factory.setInterfaces(new Class[]{interfaceClass});
        
        //创建class
        Class<?> clazz = factory.createClass();
        //实例化对象
        T t = null;
        try {
            t = interfaceClass.cast(clazz.newInstance());
        } catch (Exception e) {
            throw new RPCException(ErrorEnum.CREATE_PROXY_ERROR,"Javassist创建代理异常");
        } 
        //设置代理对象
        ((ProxyObject)t).setHandler(new MethodHandler() {
            
            @Override
            public Object invoke(Object obj, Method method, Method process, Object[] args) throws Throwable {
               return JavassistRPCProxyFactory.this.invokeProxyMethod(invoker,method,args);
            }
        });
        return t;
    }
}
