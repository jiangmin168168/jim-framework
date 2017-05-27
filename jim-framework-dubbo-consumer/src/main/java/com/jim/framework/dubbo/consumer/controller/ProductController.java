package com.jim.framework.dubbo.consumer.controller;

import com.alibaba.dubbo.rpc.RpcContext;
import com.google.common.base.Stopwatch;
import com.jim.framework.dubbo.core.model.Product;
import com.jim.framework.dubbo.core.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
        Stopwatch stopwatch=Stopwatch.createStarted();
        Product product= this.productService.getByid(productId);
        stopwatch.stop();
        logger.info("time:"+String.valueOf(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
//        Product product2= this.productService.getByid(productId*2);
//        try {
//            Product product3= (Product) RpcContext.getContext().getFuture().get();
//            System.out.println();product3.getId();
//            return product3;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        return product;
    }
}
