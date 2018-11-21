package com.weasel.secret.cloud.application;

import com.weasel.secret.common.domain.Subject;
import com.weasel.secret.common.domain.User;

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
    int delete(long id,Long userId);

    /**
     *
     * @param subject
     * @return
     */
    Subject save(Subject subject);

    /**
     *
     * @param id
     * @return
     */
    Subject findOne(Long id);
}
