package com.fast.kaca.search.web.dao;

import com.fast.kaca.search.web.entity.FileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author sys
 * @date 2019/4/16
 **/
@Repository
public interface FileDao extends CrudRepository<FileEntity, Integer> {
}
