package com.jim.framework.web.service;

import com.jim.framework.web.model.Student;

import java.util.List;

/**
 * Created by jiang on 2016/12/11.
 */
public interface StudentService {

    Student getById(Long id);

    Student save(Student student);

    List<Student> search(String name, int page, int size);

    Long getCount();
}
