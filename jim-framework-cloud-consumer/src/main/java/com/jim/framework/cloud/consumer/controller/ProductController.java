package com.jim.framework.cloud.consumer.controller;

import com.jim.framework.cloud.consumer.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;

/**
 * Created by jiang on 2017/3/28.
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping("/{productId}")
    public String getById(@PathVariable final long productId) throws UnknownHostException {
        //return restTemplate.getForEntity("http://JIM-CLOUD-PROVIDER-SERVER/product/"+productId, String.class).getBody();
        return this.productService.getById(productId);
    }
}
