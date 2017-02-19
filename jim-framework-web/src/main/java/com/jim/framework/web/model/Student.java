package com.jim.framework.web.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by jiang on 2016/12/11.
 */
@Document(indexName = "student",type = "student")
public class Student implements Serializable {
    @Id
    private Long id;

    @Field(searchAnalyzer = "ik" ,analyzer = "ik")
    @NotNull(message = "名称不能为空")
    private String name;

    private String title;

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
