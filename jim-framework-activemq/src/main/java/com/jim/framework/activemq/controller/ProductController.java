package com.jim.framework.activemq.controller;

import com.jim.framework.activemq.producer.ProductProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jiangmin on 2017/12/31.
 */


@RestController
@RequestMapping("/product")
public class ProductController{

    @Autowired
    private ProductProducer productProducer;

    @RequestMapping("/{productId}")
    public Long getById(@PathVariable final long productId) {

        this.productProducer.sendMessage(productId);
        return productId;
    }


}
