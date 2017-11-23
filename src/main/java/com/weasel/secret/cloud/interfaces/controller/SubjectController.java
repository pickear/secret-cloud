package com.weasel.secret.cloud.interfaces.controller;

import com.weasel.secret.cloud.application.SubjectService;
import com.weasel.secret.cloud.infrastructure.helper.ShiroHelper;
import com.weasel.secret.common.domain.Subject;
import com.weasel.secret.common.domain.User;
import com.weasel.secret.common.protocol.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by dell on 2017/11/13.
 */
@RestController
@RequestMapping("/subject")
@Api(value = "密码",description = "密码操作相关文档",protocols = "http/https",consumes = "application/json",produces = "application/json")
public class SubjectController {

    @Autowired
    private SubjectService service;

    @ApiOperation(
            value = "获取用户的密码记录列表",
            notes = "<h5>1.</h5>需要在登录状态下调用.<br>"
    )
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public @ResponseBody List<Subject> list(){

        User user = ShiroHelper.getCurrentUser();
        long userid = user.getId();
        List<Subject> subjects = service.findByUserId(userid);
        return subjects;
    }

    @ApiOperation(
            value = "添加/编辑用户密码记录",
            notes = "注意：<br>" +
                    "<h5>1.</h5>需要在登录状态下调用.<br>" +
                         "<h5>2.</h5>当不传id时是表示添加，传id时表示是更新.<br>" +
                         "<h5>3.</h5>第一次添加时，返回值为Subject对象，该对象带有cloud端生成的id，android端需要将此id保存，下次编辑时使用.<br>" +
                         "<h5>4.</h5>Subject对象下所有Secret的value值是用户的各种密码，应该使用用户设定的密钥加密后再传输到cloud端，cloud的数据库不保存任何跟密码有关的明文，以防被暴库时密码泄漏.<br>",
            response = Subject.class,
            httpMethod = "POST",
            consumes = "application/json",
            produces = "application/json",
            protocols = "http/https"
    )
    @ApiImplicitParam(name = "subject",value = "Subject对象",defaultValue = "NULL",required = true,dataTypeClass = Subject.class)
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public @ResponseBody Subject save(@RequestBody Subject subject){
        User user = ShiroHelper.getCurrentUser();
        long userid = user.getId();
        subject.setUserId(userid);
        subject = service.save(subject);
        return subject;
    }


    @ApiOperation(
            value = "批量添加/编辑用户密码记录",
            notes = "注意：<br>" +
                    "<h5>1.</h5>需要在登录状态下调用.<br>" +
                    "<h5>2.</h5>当不传id时是表示添加，传id时表示是更新.<br>" +
                    "<h5>2.</h5>当app端传给cloud端的用户密码记录列表为空时，cloud端什么也不做.<br>"+
                    "<h5>3.</h5>第一次添加时，返回值为Subject对象，该对象带有cloud端生成的id，android端需要将此id保存，下次编辑时使用.<br>" +
                    "<h5>4.</h5>Subject对象下所有Secret的value值是用户的各种密码，应该使用用户设定的密钥加密后再传输到cloud端，cloud的数据库不保存任何跟密码有关的明文，以防被暴库时密码泄漏.<br>",
            response = Subject.class,
            httpMethod = "POST",
            consumes = "application/json",
            produces = "application/json",
            protocols = "http/https"
    )
    @ApiImplicitParam(name = "subjects",value = "Subject对象数组",defaultValue = "NULL",required = true,dataTypeClass = List.class)
    @RequestMapping(value = "/save-list",method = RequestMethod.POST)
    public @ResponseBody List<Subject> save(@RequestBody List<Subject> subjects){
        User user = ShiroHelper.getCurrentUser();
        long userid = user.getId();
        if(!subjects.isEmpty()){
            subjects.stream().forEach(subject -> subject.setUserId(userid));
            subjects = service.save(subjects);
        }
        return subjects;
    }

    @ApiOperation(
            value = "同步用户密码记录",
            notes = "注意：<br>" +
                    "<h5>1.</h5>需要在登录状态下调用.<br>" +
                    "<h5>2.</h5>应该将当前本地所有的用户密码记录列表同步到云，如果之前同步过的记录应该带有id一起同步，否则，同步成功后cloud端会生成id回传给app端.<br>" +
                    "<h5>3.</h5>cloud端回传给app端是最新的用户密码记录列表,app端应该删除所有本地的用户密码记录列表，然后保存cloud端回传的.<br>" +
                    "<h5>4.</h5>如果app传给cloud端的用户密码记录列表是空的，表明是想删除所有cloud的密码列表，这时cloud将会<font color='red'>清除该用户下的所有密码列表</font>.<br>",
            response = Subject.class,
            httpMethod = "POST",
            consumes = "application/json",
            produces = "application/json",
            protocols = "http/https"
    )
    @ApiImplicitParam(name = "subjects",value = "Subject对象数组",defaultValue = "NULL",required = true,dataTypeClass = List.class)
    @RequestMapping(value = "/synchronize",method = RequestMethod.POST)
    public @ResponseBody List<Subject>  synchronize(@RequestBody List<Subject> subjects){
        User user = ShiroHelper.getCurrentUser();
        long userid = user.getId();
        if(!subjects.isEmpty()){
            subjects.stream().forEach(subject -> subject.setUserId(userid));
            service.save(subjects);
        }else {
            service.deleteByUserId(userid);
        }
        return service.findByUserId(userid);
    }

    @ApiOperation(
            value = "删除用户密码记录",
            notes = "<h5>1.</h5>需要在登录状态下调用.<br>" +
                    "<h5>2.</h5>只能删除属于当前登录用户的密码主体.<br>",
            response = CommonResponse.class,
            httpMethod = "DELETE",
            consumes = "application/json",
            produces = "application/json",
            protocols = "http/https"
    )
    @ApiImplicitParam(name = "id",value = "Subject ID",defaultValue = "NULL",example = "1",required = true,dataTypeClass = Long.class)
    @RequestMapping(value = "/delete",method = RequestMethod.DELETE)
    public @ResponseBody CommonResponse delete(Long id){

        User user = ShiroHelper.getCurrentUser();
        List<Subject> subjects = service.findByUserId(user.getId());
        Subject s = subjects.stream()
                            .filter(subject -> subject.getId() == id)
                            .findFirst()
                            .orElse(null);
        if(null == s){
            return CommonResponse.buildFail("id为["+id+"]的密码主体不属于当前用户");
        }
        int result = service.delete(id);
        if(result == 0){
            return CommonResponse.buildFail("删除失败");
        }
        return CommonResponse.buildSuccess("删除成功");
    }
}
