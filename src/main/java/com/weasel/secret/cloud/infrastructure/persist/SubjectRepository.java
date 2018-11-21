package com.weasel.secret.cloud.infrastructure.persist;

import com.weasel.secret.common.domain.Subject;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    /**
     *
     * @param id
     */
    @Modifying
    @Query("delete from subject s where s.id= :id and s.user_id= :userId")
    int deleteByIdAndUserId(long id,Long userId);

    /**
     *
     * @param subject
     * @return
     */
    @Modifying
    @Query("update subject set title= :title,url= :url,version=version+1 where id= :id and user_id= :userId and version = :version")
    int update(Subject subject);
}
