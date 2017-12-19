package com.weasel.secret.cloud;

import com.weasel.secret.cloud.application.SubjectService;
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
public class SubjectTests extends SecretCloudApplicationTests {

    @Autowired
    private SubjectService service;

    @Test
    public void saveList(){

        List<Subject> subjects = Lists.newArrayList();
        Subject subject = new Subject();
        subject.setTitle("平安银行");
        subject.setUrl("www.pinan.com");
        subject.setUserId(4);

        List<Secret> secrets = Lists.newArrayList();
        Secret s1 = new Secret();
        s1.setId(new Long(30));
        s1.setName("登录密码");
        s1.setValue("sdfsafdsaf");
        Secret s2 = new Secret();
        s2.setId(new Long(40));
        s2.setName("取款密码");
        s2.setValue("sfdsff");
        secrets.add(s1);
        secrets.add(s2);
        subject.setSecrets(secrets);

        subjects.add(subject);

        service.save(subjects);

    }

    @Test
    public void save(){

        Subject subject = new Subject();
        subject.setTitle("平安银行");
        subject.setUrl("www.pinan.com");
        subject.setUserId(4);
        List<Secret> secrets = Lists.newArrayList();
        Secret s1 = new Secret();
        s1.setName("登录密码");
        s1.setValue("sdfsafdsaf");
        Secret s2 = new Secret();
        s2.setName("取款密码");
        s2.setValue("sfdsff");
        secrets.add(s1);
        secrets.add(s2);
        subject.setSecrets(secrets);
        service.save(subject);

    }

    @Test
    public void deleteByUserId(){
        service.deleteByUserId(9);
    }
}
