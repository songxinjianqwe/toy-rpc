package com.sinjinsong.toy.config;

import com.sinjinsong.toy.protocol.api.Protocol;
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
public class ProtocolConfig {
    public static final Integer DEFAULT_THREADS = Integer.valueOf(100);
    public static final Integer DEFAULT_PORT = Integer.valueOf(8000);
    private String type;
    private Integer port;
    private Integer threads;
    
    private Protocol protocolInstance;
}
