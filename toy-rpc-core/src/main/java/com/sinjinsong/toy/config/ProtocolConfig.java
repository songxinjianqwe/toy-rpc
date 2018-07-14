package com.sinjinsong.toy.config;

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
    private String type;
    private Integer port;
    private Integer threads;
    private String serialize;
}
