package com.sinjinsong.toy.sample.spring.api.callback;

import java.io.Serializable;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public interface HelloCallback extends Serializable {
    void callback(String result);
}
