package com.jim.framework.web.model;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;

/**
 * Created by jiang on 2016/12/11.
 */
//@Document(indexName = "student",type = "student")
public class Student implements Serializable {
    @Id
    private Long id;

    //@Field(searchAnalyzer = "ik" ,analyzer = "ik")
    @NotNull(message = "名称不能为空")
    private String name;

    private String title;

    private int age;

    private String address;

    private double height;

    private double weight;

    private String remark;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
