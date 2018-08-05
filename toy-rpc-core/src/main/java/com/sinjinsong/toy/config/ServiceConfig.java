package com.sinjinsong.toy.config;

import com.sinjinsong.toy.protocol.api.Exporter;
import com.sinjinsong.toy.protocol.api.Invoker;
import lombok.Builder;
import lombok.Data;

/**
 * @author sinjinsong
 * @date 2018/7/7
 * <p>
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
    
    // 这里无法像ReferenceConfig一样搞一个静态cache，都从这里来发现暴露的服务
    // 因为有可能一个invoker在export之后unexport，所以从全局cache取，未必是exported
    // 建议还是从Protocol里取比较好
    public void export() {
        Invoker<T> invoker = getApplicationConfig().getProxyFactoryInstance().getInvoker(ref, interfaceClass);
        exporter = getProtocolConfig().getProtocolInstance().export(invoker, this);
    }
}
