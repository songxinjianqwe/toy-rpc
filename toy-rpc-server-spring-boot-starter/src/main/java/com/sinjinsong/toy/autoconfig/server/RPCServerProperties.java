package com.sinjinsong.toy.autoconfig.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@ConfigurationProperties(prefix = "rpc")
@Data
public class RPCServerProperties {
    private String registryAddress;
    private String serviceBasePackage;
}
