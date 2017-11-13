package com.weasel.secret.cloud.interfaces.controller;

import com.weasel.secret.cloud.application.SubjectService;
import com.weasel.secret.common.domain.Subject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by dell on 2017/11/13.
 */
@RestController
@RequestMapping("/subject")
@Api(description = "密码操作相关文档",protocols = "http")
public class SubjectController {

    @Autowired
    private SubjectService service;

    @ApiOperation(value = "获取用户的密码列表",notes = "用户权限登录")
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public List<Subject> list(){

        long userId = 1;
        List<Subject> subjects = service.findByUserId(userId);
        return subjects;
    }

    @ApiOperation(value = "添加/编辑用户密码",notes = "当不传id时是添加，传id时是更新")
    @ApiImplicitParam(name = "subject",value = "Subject对象",defaultValue = "NULL",required = true,dataTypeClass = Subject.class)
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public Subject save(Subject subject){
        subject = service.save(subject);
        return subject;
    }

    @ApiOperation(value = "删除用户密码",notes = "用户权限登录")
    @ApiImplicitParam(name = "id",value = "Subject ID",defaultValue = "NULL",example = "1",required = true,dataTypeClass = Long.class)
    @RequestMapping(value = "/delete",method = RequestMethod.DELETE)
    public String delete(Long id){
        int result = service.delete(id);
        return "";
    }
}
