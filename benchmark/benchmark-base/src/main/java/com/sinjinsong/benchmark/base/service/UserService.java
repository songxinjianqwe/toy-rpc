package com.sinjinsong.benchmark.base.service;

import com.sinjinsong.benchmark.base.domain.Page;
import com.sinjinsong.benchmark.base.domain.User;

/**
 * @author sinjinsong
 * @date 2018/8/22
 */
public interface UserService {
    boolean existUser(String email);
    
    boolean createUser(User user);

    User getUser(long id);

    Page<User> listUser(int pageNo);
}
