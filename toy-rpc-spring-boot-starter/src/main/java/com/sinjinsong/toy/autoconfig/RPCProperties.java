package com.sinjinsong.toy.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@ConfigurationProperties(prefix = "rpc")
@Data
public class RPCProperties {
    private String registryAddress;
    private String loadBalanceStrategy;
    
}
