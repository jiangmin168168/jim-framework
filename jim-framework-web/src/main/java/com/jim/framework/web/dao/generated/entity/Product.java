package com.jim.framework.web.dao.generated.entity;

import javax.persistence.*;
import java.io.Serializable;

public class Product implements Serializable {
    @Id
    private Long id;

    private String name;

    @Column(name = "product_image")
    private Object productImage;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return product_image
     */
    public Object getProductImage() {
        return productImage;
    }

    /**
     * @param productImage
     */
    public void setProductImage(Object productImage) {
        this.productImage = productImage;
    }
}