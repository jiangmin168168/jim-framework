package com.jim.framework.web.controller;

import com.jim.framework.web.common.ValueResult;
import com.jim.framework.web.model.Student;
import com.jim.framework.web.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;

/**
 * spring.data.elasticsearch
 * Created by jiang on 2016/12/5.
 */
@RestController
@RequestMapping("/student")
public class StudentController extends BaseController {

    @Autowired
    private StudentService studentService;

    @RequestMapping("/{studentId}")
    public ValueResult<Student> getById(@PathVariable final long studentId) throws UnknownHostException {

       return this.returnValueSuccess(this.studentService.getById(studentId));
    }
    @RequestMapping("/save/{studentId}")
    public ValueResult<Student> save(@PathVariable final long studentId) throws UnknownHostException {

        Student student=new Student();
        student.setId(studentId);
        student.setName("jim"+student.getId());
        student.setTitle("CEO"+student.getId());

        Student result=this.studentService.save(student);
        return this.returnValueSuccess(result);
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ValueResult<Student> save(@RequestBody @Validated Student student) throws UnknownHostException {

        Student result=this.studentService.save(student);
        return this.returnValueSuccess(result);
    }

    @RequestMapping("/count")
    public ValueResult<Long> count() throws UnknownHostException {
        return this.returnValueSuccess(this.studentService.getCount());
    }
    @RequestMapping("/search/{name}")
    public ValueResult<List<Student>> save(@PathVariable String name) throws UnknownHostException {

        List<Student> result=this.studentService.search(name,0,10);

        return this.returnValueSuccess(result);
    }
}
