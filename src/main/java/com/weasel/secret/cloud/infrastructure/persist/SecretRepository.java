package com.weasel.secret.cloud.infrastructure.persist;

import com.weasel.secret.common.domain.Secret;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author dylan
 * @date 2018/11/21
 */
@Repository
public interface SecretRepository extends CrudRepository<Secret,Long> {

    /**
     *
     * @param id
     * @return
     */
    int deleteBySubjectId(long id);
}
