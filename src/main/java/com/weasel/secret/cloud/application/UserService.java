package com.weasel.secret.cloud.application;


import com.weasel.secret.common.domain.User;

/**
 * Created by dell on 2017/11/10.
 */
public interface UserService {

    User get(long id);
}
