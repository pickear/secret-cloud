package com.weasel.secret.cloud.application;

import com.weasel.secret.common.domain.Subject;

import java.util.List;

/**
 * Created by Dylan on 2017/11/12.
 */
public interface SubjectService {

    /**
     *
     * @param id
     * @return
     */
    int delete(long id);

    /**
     *
     * @param subject
     * @return
     */
    Subject save(Subject subject);

    /**
     *
     * @param userId
     * @return
     */
    List<Subject> findByUserId(long userId);
}
