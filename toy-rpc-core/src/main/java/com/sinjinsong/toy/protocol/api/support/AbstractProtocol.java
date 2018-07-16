package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.common.util.InvocationUtil;
import com.sinjinsong.toy.config.ServiceConfig;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.invoke.api.Invocation;
import com.sinjinsong.toy.invoke.api.support.InvocationDelegate;
import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.protocol.api.Protocol;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sinjinsong
 * @date 2018/7/7
 */
@Slf4j
public abstract class AbstractProtocol implements Protocol {
    private Map<String, Invoker<?>> invokers = new ConcurrentHashMap<>();
    private Map<String, Exporter<?>> exporters = new ConcurrentHashMap<>();

    protected void putInvoker(Class<?> interfaceClass, Invoker<?> invoker) {
        this.invokers.put(interfaceClass.getName(), invoker);
    }

    protected void putExporter(Class<?> interfaceClass, Exporter<?> exporter) {
        this.exporters.put(interfaceClass.getName(), exporter);
    }

    @Override
    public <T> ServiceConfig<T> getExportedServiceConfig(String interfaceMame) throws RPCException {
        if (!exporters.containsKey(interfaceMame)) {
            throw new RPCException("未找到暴露的服务" + interfaceMame);
        }
        return (ServiceConfig<T>) exporters.get(interfaceMame).getServiceConfig();
    }

    protected <T> Invoker<T> buildFilterChain(List<Filter> filters, Invoker<T> invoker) {
        return new AbstractInvoker<T>() {
            
           private ThreadLocal<AtomicInteger> filterIndex = new ThreadLocal(){
               @Override
               protected Object initialValue() {
                   return new AtomicInteger(0);
               }
           };
            
            @Override
            public Class<T> getInterface() {
                return invoker.getInterface();
            }
            
            @Override
            protected RPCResponse doInvoke(Invocation invocation) throws RPCException {
                log.info("filterIndex:{}",filterIndex.get().get());
                Invocation originalInvocation = InvocationUtil.extractOriginalInvocation(invocation);
                if(filterIndex.get().get() < filters.size()) {
                    InvocationDelegate invocationDelegate = new InvocationDelegate(invocation,() -> doInvoke(originalInvocation));                    
                    return filters.get(filterIndex.get().getAndIncrement()).invoke(invocationDelegate);
                }
                filterIndex.get().set(0);
                return originalInvocation.invoke();
            }
        };
    }
}
