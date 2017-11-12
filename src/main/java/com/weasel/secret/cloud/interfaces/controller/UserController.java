package com.weasel.secret.cloud.interfaces.controller;

import com.weasel.secret.cloud.application.UserService;
import com.weasel.secret.common.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dell on 2017/11/10.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    @RequestMapping(path = "/query",method = RequestMethod.GET)
    public User query(long id){
        return service.get(id);
    }
}
