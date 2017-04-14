package com.jim.framework.cloud.consumer.controller;

import com.jim.framework.cloud.consumer.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${test.key}")
    private String eurekaInstanceHostname;

    @Autowired
    private ProductService productService;

    @RequestMapping("/{productId}")
    public String getById(@PathVariable final long productId) throws UnknownHostException {
        logger.info("Consumer ProductController.getById"+productId+this.eurekaInstanceHostname);
        return this.productService.getById(productId);
    }
}
