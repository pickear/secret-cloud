package com.weasel.secret.cloud.application.impl;

import com.google.common.collect.Lists;
import com.weasel.secret.cloud.application.SubjectService;
import com.weasel.secret.cloud.infrastructure.helper.GsonHelper;
import com.weasel.secret.cloud.infrastructure.persist.SubjectRepository;
import com.weasel.secret.common.domain.Secret;
import com.weasel.secret.common.domain.Subject;
import com.weasel.secret.common.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 2017/11/12.
 */
@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private static final Logger logger = LoggerFactory.getLogger(SubjectServiceImpl.class);
    @Autowired
    private SubjectRepository repository;

    @Override
    public int delete(long id) {
        try {
            repository.delete(id);
            return 1;
        }catch (Exception e){
            logger.error("delete subject [{}] is error",e);
            return 0;
        }
    }

    @Override
    public Subject save(Subject subject) {

        ingnoreNotContainSecret(subject);
        if(logger.isDebugEnabled()){
            logger.debug("save data: {}",GsonHelper.toJson(subject));
        }
        return repository.save(subject);
    }

    @Override
    public List<Subject> save(List<Subject> subjects) {

        subjects.forEach(subject -> ingnoreNotContainSecret(subject));
        if(logger.isDebugEnabled()){
            logger.debug("save data: {}",GsonHelper.toJson(subjects));
        }
        return Lists.newArrayList(repository.save(subjects));
    }

    @Override
    public List<Subject> findByUserId(long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public void deleteByUserId(long userid) {
        List<Subject> subjects = findByUserId(userid);
        repository.delete(subjects);
    }

    @Override
    public void deleteAll(List<Subject> shouldDelete) {
        repository.delete(shouldDelete);
    }

    @Override
    public List<Subject> synchronize(User user, List<Subject> subjects) {
        long userid = user.getId();
        if(!subjects.isEmpty()){
            //将需要删除的和需要更新或添加的分组
            Map<Boolean,List<Subject>> subjectGroup = subjects.stream()
                                                              .map(subject -> subject.setUserId(userid))
                                                              .collect(Collectors.groupingBy(Subject::isDeleted));

            List<Subject> userSubjects = findByUserId(userid);

            //过滤掉不属于该用户的subject，防止恶意删除。
            List<Subject> shouldDelete = subjectGroup.get(true);
            if(null != shouldDelete && !shouldDelete.isEmpty()){
                shouldDelete = ingnoreNotContain(shouldDelete,userSubjects);
                if(logger.isDebugEnabled()){
                    logger.debug("删除用户[{}]密码数据:",user.getUsername(), GsonHelper.toJson(shouldDelete));
                }
                deleteAll(shouldDelete);
            }

            //获取那些需要新增或者更新的数据。id为null需要新增，updateTime比数据库的晚要更新。
            List<Subject> shouldSave = subjectGroup.get(false);
            if(null != shouldSave && !shouldSave.isEmpty()){
                shouldSave = ingnoreShouldNotUpdate(shouldSave,userSubjects);
                if(logger.isDebugEnabled()){
                    logger.debug("保存用户[{}]密码数据:",user.getUsername(), GsonHelper.toJson(shouldSave));
                }
                save(shouldSave);
            }
        }
        return findByUserId(userid);
    }

    @Override
    public Subject findOne(Long id) {
        return repository.findOne(id);
    }

    /**
     * 过滤掉并不包含在totalSubjects的desSubjects数据
     * @param desSubjects
     * @param totalSubjects
     * @return
     */
    private List<Subject> ingnoreNotContain(List<Subject> desSubjects, List<Subject> totalSubjects){
        Assert.notNull(desSubjects,"desSubjects参数不能为null");
        Assert.notNull(desSubjects,"totalSubjects参数不能为null");

        return desSubjects.stream()
                           .filter(subject -> totalSubjects.contains(subject))
                           .collect(Collectors.toList());
    }

    /**
     *过滤掉不属于不包含在totalSecrets里的secret数据
     * @param subject
     * @param
     * @return
     */
    private void ingnoreNotContainSecret(final Subject subject){

        if(null != subject.getId()){
            List<Secret> oldSecrets = repository.findOne(subject.getId())
                                                .getSecrets();
            if(logger.isDebugEnabled()){
                logger.debug("current data: {}",GsonHelper.toJson(oldSecrets));
            }
            List<Secret> secrets =  subject.getSecrets()
                                           .stream()
                                           .filter(secret -> {
                                               boolean include = (secret.getId() == null || oldSecrets.contains(secret));
                                               if(logger.isDebugEnabled()){
                                                   if(include){
                                                       logger.debug("secret [{}] will be inclue...",secret.getId());
                                                   }
                                               }
                                               return include;
                                           })
                                           .collect(Collectors.toList());
            if(logger.isDebugEnabled()){
                logger.debug("filted secret is : {}",GsonHelper.toJson(secrets));
            }
            subject.setSecrets(secrets);
        }else {
            subject.getSecrets()
                    .forEach(secret -> secret.setId(null));
        }
    }

    /**
     * 过滤掉那些不需要更新的记录，保留需要更新或者新增的
     * @param desSubjects
     * @param totalSubjects
     * @return
     */
    private List<Subject> ingnoreShouldNotUpdate(List<Subject> desSubjects, List<Subject> totalSubjects){
        Assert.notNull(desSubjects,"desSubjects参数不能为null");
        Assert.notNull(desSubjects,"totalSubjects参数不能为null");

        return desSubjects.stream()
                            .filter(subject -> {
                                Subject _suject = totalSubjects.stream()
                                                               .filter(_subject -> subject.getId() == _subject.getId())
                                                               .findFirst()
                                                               .orElse(null);
                                //id 为null说明是需要新增的，_subject为null说明不存在，也是需要新增的。更新时间比数据库的时候晚的话，需要更新。
                                return null == subject.getId() || null == _suject || _suject.getUpdateTime() < subject.getUpdateTime();
                            })
                            .map(subject -> {
                                Long currentTime = System.currentTimeMillis();
                                subject.setUpdateTime(currentTime);
                                if (null == subject.getId()){
                                    subject.setCreateTime(currentTime);
                                }
                                return subject;
                            })
                            .collect(Collectors.toList());
    }
}
