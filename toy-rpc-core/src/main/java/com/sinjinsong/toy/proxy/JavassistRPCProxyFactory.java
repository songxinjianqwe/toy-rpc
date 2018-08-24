package com.sinjinsong.toy.proxy;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.proxy.api.support.AbstractRPCProxyFactory;
import javassist.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author sinjinsong
 * @date 2018/8/23
 */
@Slf4j
public class JavassistRPCProxyFactory extends AbstractRPCProxyFactory {
    private CtClass invokerCtClass = new CtClass(Invoker.class.getName()) {
    };
    
    private CtClass interceptorCtClass = new CtClass(AbstractRPCProxyFactory.class.getName()) {
    };
    

    @Override
    protected <T> T doCreateProxy(Class<T> interfaceClass, Invoker<T> invoker) {
        T t = null;
        try {
            String interfaceName = interfaceClass.getName();
            ClassPool pool = ClassPool.getDefault();
            // 传入类名，最后生成某种Interface
            // 我们保证某个interface只会生成一个代理类
            CtClass proxyClass = pool.makeClass(interfaceName + "$proxyInvoker");
            // 设置接口类型
            proxyClass.setInterfaces(new CtClass[]{pool.getCtClass(interfaceName)});
            CtField invokerField = new CtField(this.invokerCtClass, "invoker", proxyClass);
            invokerField.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
            proxyClass.addField(invokerField);
            CtField interceptorField = new CtField(this.interceptorCtClass, "interceptor", proxyClass);
            interceptorField.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
            proxyClass.addField(interceptorField);

            CtConstructor ctConstructor = new CtConstructor(new CtClass[]{this.invokerCtClass, this.interceptorCtClass}, proxyClass);
            ctConstructor.setModifiers(Modifier.PUBLIC);
            ctConstructor.setBody("{this.invoker=$1;this.interceptor=$2;}");
            proxyClass.addConstructor(ctConstructor);
            Method[] methods = interfaceClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                addInterfaceMethod(interfaceName, proxyClass, method);
            }
            addCommonMethods(interfaceName, proxyClass);
            t = interfaceClass.cast(proxyClass.toClass().getConstructor(Invoker.class, AbstractRPCProxyFactory.class).newInstance(invoker, this));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException(ErrorEnum.CREATE_PROXY_ERROR, "生成Javassist动态代理失败", e);
        }
        return t;
    }

    private void addCommonMethods(String interfaceName, CtClass proxyClass) {
//        if (methodName.equals("toString") && parameterTypes.length == 0) {
//            methodDeclare.append("return invokerCtClass.toString();}");
//        } else if ("hashCode".equals(method.getName()) && method.getParameterTypes().length == 0) {
//            methodDeclare.append("return invokerCtClass.hashCode();}");
//        } else if ("equals".equals(method.getName()) && method.getParameterTypes().length == 1) {
//            methodDeclare.append("invokerCtClass.equals(args[0]);}");
//        } else {
    }

    private static void addInterfaceMethod(String classToProxy, CtClass implementer, Method method) throws CannotCompileException {
        String methodCode = generateMethodCode(classToProxy, method);
        CtMethod cm = CtNewMethod.make(methodCode, implementer);
        implementer.addMethod(cm);
    }

    private static String generateMethodCode(String interfaceName, Method method) {
        String methodName = method.getName();
        String methodReturnType = method.getReturnType().getName();
        Class[] parameterTypes = method.getParameterTypes();
        Class[] exceptionTypes = method.getExceptionTypes();

        //组装方法的Exception声明  
        StringBuilder exceptionBuffer = new StringBuilder();
        if (exceptionTypes.length > 0) exceptionBuffer.append(" throws ");
        for (int i = 0; i < exceptionTypes.length; i++) {
            if (i != exceptionTypes.length - 1) {
                exceptionBuffer.append(exceptionTypes[i].getName()).append(",");
            } else {
                exceptionBuffer.append(exceptionTypes[i].getName());
            }
        }

        //组装方法的参数列表  
        StringBuilder parameterBuffer = new StringBuilder();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class parameter = parameterTypes[i];
            String parameterType = parameter.getName();
            //动态指定方法参数的变量名  
            String refName = "a" + i;
            parameterBuffer.append(parameterType).append(" " + refName);
            if (i != parameterTypes.length - 1) {
                parameterBuffer.append(",");
            }
        }

        //方法声明，由于是实现接口的方法，所以是public  
        StringBuilder methodDeclare = new StringBuilder();
        methodDeclare.append("public ").append(methodReturnType).append(" ").append(methodName).append("(").append(parameterBuffer).append(")").append(exceptionBuffer).append(" {");
//        methodDeclare.append("System.out.println(a0);");     
//        methodDeclare.append("System.out.println(new Object[]{a0});");
        // 方法体
        methodDeclare.append("return interceptor.invokeProxyMethod(")
                .append("invoker").append(",")
                .append("\"")
                .append(interfaceName).append("\"")
                .append(",")
                .append("\"").append(methodName).append("\"")
                .append(",")
                .append("new String[]{");
        for (int i = 0; i < parameterTypes.length; i++) {
            methodDeclare.append("\"").append(parameterTypes[i].getName()).append("\"");
        }
        methodDeclare.append("}");
        methodDeclare.append(",");
        //传递方法里的参数  
        methodDeclare.append("new Object[]{");
        for (int i = 0; i < parameterTypes.length; i++) {
            methodDeclare.append("a").append(i);
            if (i != parameterTypes.length - 1) {
                methodDeclare.append(",");
            }
        }
        methodDeclare.append("});}");
        System.out.println(methodDeclare.toString());
        return methodDeclare.toString();
    }
}
