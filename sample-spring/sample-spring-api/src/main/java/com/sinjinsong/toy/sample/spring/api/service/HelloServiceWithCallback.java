package com.sinjinsong.toy.sample.spring.api.service;

import com.sinjinsong.toy.sample.spring.api.callback.HelloCallback;
import com.sinjinsong.toy.sample.spring.api.domain.User;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public interface HelloServiceWithCallback {
     void hello(User user, HelloCallback callback);
}
