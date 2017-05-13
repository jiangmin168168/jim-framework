package com.jim.framework.rpc.api.model;

import java.io.Serializable;

/**
 * Created by jiang on 2017/5/9.
 */
public class Comment implements Serializable {

    private Long productId;
    private Long id;
    private String content;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
