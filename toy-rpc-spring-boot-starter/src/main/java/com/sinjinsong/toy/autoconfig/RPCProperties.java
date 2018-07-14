package com.sinjinsong.toy.autoconfig;

import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.config.ClusterConfig;
import com.sinjinsong.toy.config.ProtocolConfig;
import com.sinjinsong.toy.config.RegistryConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@ConfigurationProperties(prefix = "rpc")
@Data
public class RPCProperties {
    private ProtocolConfig protocol;
    private ApplicationConfig application;
    private RegistryConfig registry;
    private ClusterConfig cluster;
}
