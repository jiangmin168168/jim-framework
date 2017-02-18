package com.jim.controller;

import com.jim.common.ValueResult;
import com.jim.dao.generated.entity.Product;
import com.jim.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;

/**
 * Created by jiang on 2016/12/5.
 */
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    @RequestMapping("/{productId}")
    public ValueResult<Product> getById( @PathVariable final long productId) throws UnknownHostException {
        Product result= this.productService.getById(productId);
        return this.returnValueSuccess(result);

    }
    @RequestMapping("/save/{productId}")
    public ValueResult<String> save(@PathVariable final long productId) throws UnknownHostException {

        Product product=new Product();
        product.setId(productId);
        product.setName("jim-product-"+productId);
        this.productService.save(product);
        return this.returnValueSuccess("success");

    }

}
