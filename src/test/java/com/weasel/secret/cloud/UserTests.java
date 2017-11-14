package com.weasel.secret.cloud;

import com.weasel.secret.cloud.application.UserService;
import com.weasel.secret.common.domain.Secret;
import com.weasel.secret.common.domain.Subject;
import com.weasel.secret.common.domain.User;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Dylan on 2017/11/12.
 */
public class UserTests extends SecretCloudApplicationTests {

    @Autowired
    private UserService service;

    @Test
    public void save(){
        User user = new User();
        user.setId(5);
        user.setUsername("ccc");
        user.setPassword("234dsfsser");
        user.setEmail("2234234@qq.com");

        List<Subject> subjects = Lists.newArrayList();
        Subject subject = new Subject();
        subject.setTitle("平安银行");
        subject.setUrl("www.pingan.com");

        List<Secret> secrets = Lists.newArrayList();
        Secret s1 = new Secret();
        s1.setName("登录密码");
        s1.setValue("aaaaaa");
        Secret s2 = new Secret();
        s2.setName("取款密码");
        s2.setValue("bbbbbbbb");
        secrets.add(s1);
        secrets.add(s2);
        subject.setSecrets(secrets);

        subjects.add(subject);

        user.setSubjects(subjects);
        service.save(user);
    }
}
