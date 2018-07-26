package com.sinjinsong.toy.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sinjinsong
 * @date 2018/7/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Executors {
    private ExecutorConfig client;
    private ExecutorConfig server;
    
    public void close() {
        if(client != null) {
            client.close();
        }
        if(server != null) {
            server.close();
        }
    }
}
