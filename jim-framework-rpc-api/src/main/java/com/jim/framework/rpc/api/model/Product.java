package com.jim.framework.rpc.api.model;

import java.io.Serializable;

/**
 * Created by jiang on 2017/5/10.
 */
public class Product implements Serializable {

    private Long id;
    private String name;

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
}
