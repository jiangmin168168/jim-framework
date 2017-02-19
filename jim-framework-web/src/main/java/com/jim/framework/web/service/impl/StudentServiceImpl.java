package com.jim.framework.web.service.impl;

import com.jim.framework.web.model.Student;
import com.jim.framework.web.service.BaseService;
import com.jim.framework.web.service.StudentService;
import org.springframework.stereotype.Service;
import com.jim.framework.web.repository.StudentRepository;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * Created by jiang on 2016/12/11.
 */
@Service
public class StudentServiceImpl extends BaseService implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Cacheable(value = "StudentService.getById")
    @Override
    public Student getById(Long id) {
        return this.getByIdCache(id);
    }

    //@Cacheable(value = "StudentService.getById")
    public Student getByIdCache(Long id) {
        this.logger.info("get student from es");
        return studentRepository.findOne(id);
    }

    @Override
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public List<Student> search(String name,int page, int size){

        BoolQueryBuilder keyQueryBuilder= QueryBuilders.boolQuery();
        MatchQueryBuilder idQueryBuild=QueryBuilders.matchQuery("title",name);
        MatchQueryBuilder nameQueryBuild=QueryBuilders.matchQuery("name",name);
        keyQueryBuilder.should(idQueryBuild);
        keyQueryBuilder.should(nameQueryBuild);

        Page<Student> students=this.studentRepository.search(keyQueryBuilder,new PageRequest(page,size));

        return students.getContent();
    }

    @Override
    public Long getCount() {
        return this.studentRepository.count();
    }
}
