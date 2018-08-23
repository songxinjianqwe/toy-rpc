package com.sinjinsong.benchmark.toy.server;

import com.sinjinsong.benchmark.base.service.UserService;
import com.sinjinsong.benchmark.base.service.impl.UserServiceBaseImpl;
import com.sinjinsong.toy.autoconfig.annotation.RPCService;

/**
 * @author sinjinsong
 * @date 2018/8/23
 */
@RPCService(interfaceClass = UserService.class)
public class UserServiceImpl extends UserServiceBaseImpl {
}
