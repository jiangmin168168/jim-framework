package com.jim.framework.web.repository;

import com.jim.framework.web.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by jiang on 2016/12/11.
 */
public interface StudentRepository extends ElasticsearchRepository<Student,Long>{

    Page<Student> findByName(String name, Pageable pageable);
}
