package com.weasel.secret.cloud.interfaces.controller;

import com.weasel.secret.cloud.application.UserService;
import com.weasel.secret.cloud.infrastructure.helper.ShiroHelper;
import com.weasel.secret.common.domain.User;
import com.weasel.secret.common.protocol.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by dell on 2017/11/10.
 */
@RestController
@RequestMapping("/user")
@Api(description = "用户操作相关文档",protocols = "http")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService service;

    @ApiOperation(value = "获取用户信息",notes = "通过接口获取用户的信息")
    @RequestMapping(path = "/query",method = RequestMethod.GET)
    public @ResponseBody User query(){
        User user = ShiroHelper.getCurrentUser();
        return service.findByUsername(user.getUsername());
    }

    /**
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "用户注册",notes = "用户注册一个帐号")
    @ApiImplicitParam(name = "user",value = "用户对象",defaultValue = "NULL",required = true,dataTypeClass = User.class)
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public @ResponseBody User register(@RequestBody  User user){

        Assert.notNull(user,"user can not be null");
        Assert.hasLength(user.getUsername(),"username can not be empty");
        Assert.hasLength(user.getPassword(),"password can not be empty");
        logger.info("用户[{}]注册！",user.getUsername());
        User existUser = service.findByUsername(user.getUsername());
        if (null != existUser){
            return user;
        }
        user.encodePassword();
        user = service.save(user);
        return user;
    }

    @ApiOperation(value = "登录",notes = "用户权限登录")
    @ApiImplicitParam(name = "user",value = "用户对象",defaultValue = "NULL",required = true,dataTypeClass = User.class)
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public @ResponseBody CommonResponse login(@RequestBody User user, HttpServletRequest request){

        Assert.notNull(user,"user can not be null");
        Assert.hasLength(user.getUsername(),"username can not be empty");
        Assert.hasLength(user.getPassword(),"password can not be empty");
        logger.info("用户[{}]登录！",user.getUsername());
        try {
            Subject currentUser = SecurityUtils.getSubject();
            if (currentUser.isAuthenticated()) {
                currentUser.logout();
            }
            boolean rememberMe = ServletRequestUtils.getBooleanParameter(request, "rememberMe", false);
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword(), rememberMe);
            currentUser.login(token); // 登录
        }catch (IncorrectCredentialsException exception){
            logger.warn("用户[{}]输入的密码不正确。",user.getUsername());
            return CommonResponse.buildFail("密码不正确");
        }catch (UnknownAccountException exception){
            logger.warn("用户名[{}]不存在。",user.getUsername());
            return CommonResponse.buildFail("该用户不存在!");
        }
        logger.info("用户[{}]登录成功!",user.getUsername());
        return CommonResponse.buildSuccess("登录成功");
    }

    @ApiOperation(value = "是否已登录",notes = "判断用户是否已登录")
    @RequestMapping(value = "/hadLogin",method = RequestMethod.GET)
    public @ResponseBody CommonResponse hadLogin(){

        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
           return CommonResponse.buildSuccess("用户已登录");
        }
        return CommonResponse.buildFail("用户未登录");
    }
}
