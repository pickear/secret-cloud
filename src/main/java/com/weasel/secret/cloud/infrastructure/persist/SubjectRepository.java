package com.weasel.secret.cloud.infrastructure.persist;

import com.weasel.secret.common.domain.Subject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Dylan on 2017/11/12.
 */
@Repository
public interface SubjectRepository extends CrudRepository<Subject,Long>{

    /**
     *
     * @param userId
     * @return
     */
    List<Subject> findByUserId(long userId);
}
