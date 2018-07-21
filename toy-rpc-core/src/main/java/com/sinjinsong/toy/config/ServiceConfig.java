package com.sinjinsong.toy.config;

import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import lombok.Builder;
import lombok.Data;

/**
 * @author sinjinsong
 * @date 2018/7/7
 * 
 * 暴露服务配置
 */
@Data
@Builder
public class ServiceConfig<T> extends AbstractConfig {
    private String interfaceName;
    private Class<T> interfaceClass;
    /**
     * 表示这个接口是有一个callback参数用来回调，而不是这个接口是一个回调接口
     */
    private boolean isCallback;
    private String callbackMethod;
    private int callbackParamIndex = 1;
    private boolean isCallbackInterface;
    private T ref;
    
    private Exporter<T> exporter; 
    
    public void export() {
        Invoker<T> invoker = applicationConfig.getProxyFactoryInstance().getInvoker(ref, interfaceClass);
        exporter = protocolConfig.getProtocolInstance().export(invoker,this);
    }
}
