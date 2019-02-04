package com.weasel.secret.cloud.infrastructure.persist;

import com.weasel.secret.common.domain.Subject;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Dylan on 2017/11/12.
 */
@Repository
public interface SubjectRepository extends CrudRepository<Subject,Long>{


    /**
     *
     * @return
     */
    Subject findOneByIdAndDeletedIsFalse(Long id);

    /**
     *
     * @return
     */
    List<Subject> findAllByDeletedIsFalse();
    /**
     *
     * @param userId
     * @return
     */
    List<Subject> findByUserIdAndDeletedIsFalse(long userId);

    /**
     *
     * @param id
     */
    @Modifying
    @Query("update Subject s set s.deleted=true where s.id= :id and s.userId= :userId")
    int deleteByIdAndUserId(@Param("id") long id,@Param("userId") Long userId);

    /**
     *
     * @param subject
     * @return
     */
    @Modifying
    @Query("update Subject s set s.title= :#{#subject.title},s.url= :#{#subject.url},s.version=s.version+1 where s.id= :#{#subject.id} and s.userId= :#{#subject.userId} and s.version = :#{#subject.version}")
    int update(@Param("subject") Subject subject);
}
