package com.fast.kaca.search.web.dao;

import com.fast.kaca.search.web.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author sys
 * @date 2019/4/16
 **/
@Repository
public interface UserDao extends CrudRepository<UserEntity, Integer> {

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    UserEntity findByUserName(String userName);

}
