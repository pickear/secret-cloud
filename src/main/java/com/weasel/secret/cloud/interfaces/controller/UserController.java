package com.weasel.secret.cloud.interfaces.controller;

import com.weasel.secret.cloud.application.UserService;
import com.weasel.secret.common.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dell on 2017/11/10.
 */
@RestController
@RequestMapping("/user")
@Api(description = "用户操作相关文档",protocols = "http")
public class UserController {

    @Autowired
    private UserService service;

    @ApiOperation(value = "获取用户信息",notes = "通过接口获取用户的信息")
    @ApiImplicitParam(name = "id",value = "用户Id",defaultValue = "NULL",example = "1",required = true,dataTypeClass = Long.class)
    @RequestMapping(path = "/query",method = RequestMethod.GET)
    public User query(Long id){
        return service.get(id);
    }

    /**
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "用户注册",notes = "用户注册一个帐号")
    @ApiImplicitParam(name = "user",value = "用户对象",defaultValue = "NULL",required = true,dataTypeClass = User.class)
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public User register(User user){

        user = service.save(user);
        return user;
    }

    @ApiOperation(value = "登录",notes = "用户权限登录")
    @ApiImplicitParam(name = "user",value = "用户对象",defaultValue = "NULL",required = true,dataTypeClass = User.class)
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(User user){
        return "";
    }
}
