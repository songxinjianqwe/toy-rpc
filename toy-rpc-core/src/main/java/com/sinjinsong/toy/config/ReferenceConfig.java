package com.sinjinsong.toy.config;

import lombok.Builder;
import lombok.Data;

/**
 * @author sinjinsong
 * @date 2018/7/7
 * 
 * 引用服务配置
 */
@Data
@Builder
public class ReferenceConfig<T> {
    private String interfaceName;
    private Class<T> interfaceClass;
    private boolean isAsync;
    private boolean isCallback;
    private long timeout =  3000;
    private String callbackMethod;
    private int callbackParamIndex = 1;
}
