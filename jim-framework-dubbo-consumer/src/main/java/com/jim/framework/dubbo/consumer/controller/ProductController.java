package com.jim.framework.dubbo.consumer.controller;

import com.jim.framework.dubbo.core.model.Product;
import com.jim.framework.dubbo.core.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @RequestMapping("/{productId}")
    public Product getById(@PathVariable final long productId) throws UnknownHostException {
        logger.info("ProductController.getById");
        return this.productService.getByid(productId);
    }
}
