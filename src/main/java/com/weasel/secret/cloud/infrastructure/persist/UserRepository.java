package com.weasel.secret.cloud.infrastructure.persist;

import com.weasel.secret.cloud.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by dell on 2017/11/10.
 */
@Repository
public interface UserRepository extends CrudRepository<User,Long> {
}
