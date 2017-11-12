package com.weasel.secret.cloud.application.impl;

import com.weasel.secret.cloud.application.UserService;
import com.weasel.secret.cloud.infrastructure.persist.UserRepository;
import com.weasel.secret.common.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dell on 2017/11/10.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    public User get(long id) {
        return repository.findOne(id);
    }
}
