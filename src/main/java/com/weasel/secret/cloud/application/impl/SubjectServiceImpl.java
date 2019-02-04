package com.weasel.secret.cloud.application.impl;

import com.google.common.collect.Lists;
import com.weasel.secret.cloud.application.SubjectService;
import com.weasel.secret.cloud.infrastructure.helper.GsonHelper;
import com.weasel.secret.cloud.infrastructure.persist.SecretRepository;
import com.weasel.secret.cloud.infrastructure.persist.SubjectRepository;
import com.weasel.secret.common.domain.Secret;
import com.weasel.secret.common.domain.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Created by Dylan on 2017/11/12.
 */
@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private static final Logger logger = LoggerFactory.getLogger(SubjectServiceImpl.class);
    @Autowired
    private SubjectRepository repository;
    @Autowired
    private SecretRepository secretRepository;

    @Override
    public int delete(long id,Long userId) {
        try {
            int subjectCount = repository.deleteByIdAndUserId(id,userId);
            return subjectCount;
        }catch (Exception e){
            logger.error("delete subject [{}] is error",e);
            return 0;
        }
    }

    @Override
    public Subject save(Subject subject) {

        if(logger.isDebugEnabled()){
            logger.debug("save data: {}",GsonHelper.toJson(subject));
        }
        if(Objects.isNull(subject.getId())){
            return repository.save(subject);
        }

        int updateCount = repository.update(subject);
        Assert.isTrue(updateCount > 0,"更新失败，数据不是最新，请先更新!");
        Iterable<Secret> secrets = secretRepository.save(subject.getSecrets());
        subject.setSecrets(Lists.newArrayList(secrets));
        return findOne(subject.getId());
    }

    @Override
    public Subject findOne(Long id) {
        return repository.findOneByIdAndDeletedIsFalse(id);
    }

}
