package com.weasel.secret.cloud.application;


import com.weasel.secret.common.domain.User;

/**
 * Created by dell on 2017/11/10.
 */
public interface UserService {

    /**
     * 通过用户id获取用户信息
     * @param id
     * @return
     */
    User get(long id);

    /**
     * 保存用户信息
     * @param user
     * @return
     */
    User save(User user);
}
