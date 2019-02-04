package com.weasel.secret.cloud;

import com.google.gson.reflect.TypeToken;
import com.weasel.secret.cloud.application.SubjectService;
import com.weasel.secret.cloud.infrastructure.helper.GsonHelper;
import com.weasel.secret.cloud.infrastructure.persist.SubjectRepository;
import com.weasel.secret.common.domain.Secret;
import com.weasel.secret.common.domain.Subject;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 2017/11/12.
 */
public class SubjectTests extends SecretCloudApplicationTests{

    @Autowired
    private SubjectService service;
    @Autowired
    private SubjectRepository repository;

    @Test
    public void add(){

        Subject subject = new Subject();
        subject.setTitle("平安银行");
        subject.setUrl("www.pinan.com");
        subject.setUserId(0l);
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
    public void upodate(){
        List<Subject> subjects = Lists.newArrayList(repository.findAll());
        if(null != subjects && !subjects.isEmpty()){
            Subject subject = subjects.get(0);
            Subject subject2 = service.save(subject);
            Assert.assertTrue(subject.getVersion()+1 == subject2.getVersion() );
        }
    }

    @Test
    public void delete(){
        List<Subject> subjects = Lists.newArrayList(repository.findAllByDeletedIsFalse());
        if(null != subjects && !subjects.isEmpty()){
            int count = service.delete(subjects.get(0).getId(),subjects.get(0).getUserId());
            Assert.assertTrue(count > 0 );
        }
    }

    @Test
    public void list(){
        List<Subject> subjects = Lists.newArrayList(repository.findAllByDeletedIsFalse());
        if(null != subjects && !subjects.isEmpty()){
            for(Subject s : subjects){
                Assert.assertTrue(!s.isDeleted());
            }
        }
    }

    @Test
    public void contain() {

        String totalData = "[{\"id\":127,\"name\":\"服务密码\",\"value\":\"wR4Rap4vuZw\\u003d\\n\"},{\"id\":159,\"name\":\"登录密码\",\"value\":\"qreFVmx0qQI\\u003d\\n\"},{\"id\":160,\"name\":\"hcygv\",\"value\":\"bhlhLofPxzk\\u003d\\n\"},{\"name\":\"hhhhhhh\",\"value\":\"mkqJ6RhzMHo\\u003d\\n\"}]";
        String currentData = "[{\"id\":127,\"name\":\"服务密码\",\"value\":\"wR4Rap4vuZw\\u003d\\n\"},{\"id\":159,\"name\":\"登录密码\",\"value\":\"qreFVmx0qQI\\u003d\\n\"},{\"id\":160,\"name\":\"hcygv\",\"value\":\"bhlhLofPxzk\\u003d\\n\"}]";

        List<Secret> totalSecrets = GsonHelper.fromJson(totalData,new TypeToken<List<Secret>>(){});

        List<Secret> currentSecrets = GsonHelper.fromJson(currentData,new TypeToken<List<Secret>>(){});

        List<Secret> result = totalSecrets.stream().filter(secret -> {
            boolean include = (secret.getId() == null || currentSecrets.contains(secret));
            return include;
        })
                .collect(Collectors.toList());
        System.out.println(GsonHelper.toJson(result));
    }
}
