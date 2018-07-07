package com.sinjinsong.toy.config;

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
public class ServiceConfig<T> {
    private String interfaceName;
    private Class<T> interfaceClass;
    private boolean isCallback;
    private String callbackMethod;
    private int callbackParamIndex = 1;
}
