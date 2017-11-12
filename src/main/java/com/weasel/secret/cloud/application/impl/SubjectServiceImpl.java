package com.weasel.secret.cloud.application.impl;

import com.weasel.secret.cloud.application.SubjectService;
import com.weasel.secret.cloud.infrastructure.persist.SubjectRepository;
import com.weasel.secret.common.domain.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Dylan on 2017/11/12.
 */
@Service
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
        return repository.save(subject);
    }
}
