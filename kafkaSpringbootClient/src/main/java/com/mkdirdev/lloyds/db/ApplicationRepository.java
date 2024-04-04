package com.mkdirdev.lloyds.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends CrudRepository<AppTable,Long> {
    
}
