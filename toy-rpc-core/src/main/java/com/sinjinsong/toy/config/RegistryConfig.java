package com.sinjinsong.toy.config;

import com.sinjinsong.toy.registry.api.ServiceRegistry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryConfig {
    private String type;
    private String address;
    private ServiceRegistry registryInstance;
    
    public void init() {
        if(registryInstance != null) {
            registryInstance.init();
        }
    }
    
    
    public void close() {
        if(registryInstance != null) {
            registryInstance.close();
        }
    }
}
