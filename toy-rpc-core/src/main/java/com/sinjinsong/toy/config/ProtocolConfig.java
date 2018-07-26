package com.sinjinsong.toy.config;

import com.sinjinsong.toy.protocol.api.Protocol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ProtocolConfig {

    public static final Integer DEFAULT_PORT = Integer.valueOf(8000);
    private String type;
    private Integer port;

    private Protocol protocolInstance;
    private ExecutorConfig executor;

    public int getPort() {
        if (port != null) {
            return port;
        }
        return DEFAULT_PORT;
    }
    
    public void close() {
        protocolInstance.close();
    }
}
