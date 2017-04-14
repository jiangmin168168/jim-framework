package com.jim.framework.dubbo.core.model;

import java.io.Serializable;

/*
* 评论实体
* 作者：姜敏
* 版本：V1.0
* 创建日期：2017/4/14
* 修改日期:2017/4/14
*/
public class Comment implements Serializable {
    private Long id;

    private String content;

    private Long userId;

    private Long productId;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
